package br.com.fiap.mspurchases.service;

import br.com.fiap.mspurchases.entity.*;
import br.com.fiap.mspurchases.functions.CustomerFunction;
import br.com.fiap.mspurchases.functions.ProductFunction;
import br.com.fiap.mspurchases.functions.PurchaseHistoryFunction;
import br.com.fiap.mspurchases.repository.PurchaseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalesService {
	public static final String PURCHASE_NOT_FOUND = "Pedido não encontrado"; //Purchase not found
	public static final String CUSTOMER_NOT_FOUND = "Cliente não encontrado"; //Customer not found
	public static final String PRODUCT_NOT_FOUND = "Item %s não encontrado"; //Product not found
	public static final String JSON_ERROR = "Erro ao processar JSON"; //Error processing JSON
	public static final String PRODUCT_DOESNT_HAVE_ENOUGH_STOCK = "Estoque insuficiente para o produto %s"; //Insufficient stock
	public static final String STATUS_NOT_VALID = "Não foi possível mudar o pedido do status %s para o status %s"; //Cannot change to this new status based on last status

	private static final String WAITING_PAYMENT_STATUS = "AGUARDANDO PAGAMENTO";
	private static final String PAID_STATUS = "PAGO";
	private static final String CANCELED_STATUS = "CANCELADO";

	private final PurchaseRepository purchaseRepository;
	private final CustomerFunction customerFunction;
	private final ProductFunction productFunction;
	private final PurchaseHistoryFunction purchaseHistoryFunction;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	public SalesService(
			PurchaseRepository purchaseRepository,
			CustomerFunction customerFunction,
			ProductFunction productFunction,
			PurchaseHistoryFunction purchaseHistoryFunction,
			RestTemplate restTemplate,
			ObjectMapper objectMapper) {
		this.purchaseRepository = purchaseRepository;
		this.customerFunction = customerFunction;
		this.productFunction = productFunction;
		this.purchaseHistoryFunction = purchaseHistoryFunction;
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
	}

	public Purchase getPurchaseById(Long id) {
		return this.purchaseRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException(PURCHASE_NOT_FOUND));
	}

	public List<Purchase> getPurchasesByStatus(String status) {
		return this.purchaseRepository.findByStatusIgnoreCase(status);
	}

	public Purchase createPurchase(Purchase purchase) {
		purchase.setStatus(WAITING_PAYMENT_STATUS); //Initial status value. Waiting for payment
		setCustomerData(purchase); //Set customer data
		setTotalValue(purchase); //Set total value

		Purchase savedPurchase = this.purchaseRepository.save(purchase);
		removeProductsFromStock(savedPurchase); //Remove product from stock
		saveHistory(savedPurchase); //Save history

		return savedPurchase;
	}

	public Purchase payPurchase(Long id) {
		return updatePurchase(id, PAID_STATUS);
	}

	public Purchase cancelPurchase(Long id) {
		return updatePurchase(id, CANCELED_STATUS);
	}

	private Purchase updatePurchase(Long id, String status){
		Purchase purchaseToUpdate = this.purchaseRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(PURCHASE_NOT_FOUND));

		//If the status is different from WAITING_PAYMENT_STATUS, it is not possible to change the status
		if(!purchaseToUpdate.getStatus().equals(WAITING_PAYMENT_STATUS)){
			throw new DataIntegrityViolationException(
					String.format(STATUS_NOT_VALID, purchaseToUpdate.getStatus(), status)
			) ;
		}

		purchaseToUpdate.setStatus(status);
		Purchase savedPurchase = this.purchaseRepository.save(purchaseToUpdate);
		saveHistory(savedPurchase); //Save history

		return savedPurchase;
	}

	private void removeProductsFromStock(Purchase savedPurchase) {
		for (PurchaseItem item : savedPurchase.getItems()) {
			Product product = new Product();
			product.setId(item.getProductId());
			product.setQuantity(item.getQuantity());
			productFunction.removeStock(product);
		}
	}

	private void setTotalValue(Purchase purchase) {
		float totalValue = 0.0F;

		for(PurchaseItem item : purchase.getItems()) {
			try {
				ResponseEntity<String> response = restTemplate.getForEntity(
						"http://msstock:8082/stock/getProductById/{id}",
						String.class,
						item.getProductId()
				);

				try {
					JsonNode jsonProduct = objectMapper.readTree(response.getBody());
					int quantity = jsonProduct.get("quantity").asInt();
					float price = jsonProduct.get("price").floatValue();

					if (quantity < item.getQuantity()) {
						throw new DataIntegrityViolationException(String.format(PRODUCT_DOESNT_HAVE_ENOUGH_STOCK, item.getProductId()));
					}
					totalValue += price * item.getQuantity();
				} catch (JsonProcessingException e) {
					throw new DataIntegrityViolationException(JSON_ERROR);
				}
			}catch (HttpClientErrorException e){
				throw new EntityNotFoundException(String.format(PRODUCT_NOT_FOUND, item.getProductId()));
			}
		}
		purchase.setTotalAmount(totalValue);
	}

	private void setCustomerData(Purchase purchase) {
		try {
			Customer customer = customerFunction.findCustomer(purchase.getCustomerCpf());
			purchase.setCustomerName(customer.getFullName());
			purchase.setDeliveryZipCode(customer.getZipCode());
			purchase.setDeliveryAddress(customer.getAddress() + ", " + customer.getCity() + " - " + customer.getState() + ", " + customer.getCountry()); //Get the complete address
		}
		catch (Exception e){
			throw new EntityNotFoundException(CUSTOMER_NOT_FOUND);
		}
	}

	private void saveHistory(Purchase savedPurchase) {
		PurchaseHistory purchaseHistory = new PurchaseHistory();

		purchaseHistory.setPurchaseId(savedPurchase.getId());
		purchaseHistory.setStatus(savedPurchase.getStatus());
		purchaseHistory.setStatusDate(LocalDateTime.now());

		purchaseHistoryFunction.saveHistory(purchaseHistory);
	}

}
