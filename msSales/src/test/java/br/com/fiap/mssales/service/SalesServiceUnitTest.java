package br.com.fiap.mssales.service;

import br.com.fiap.mssales.entity.*;
import br.com.fiap.mssales.functions.CustomerFunction;
import br.com.fiap.mssales.functions.ProductFunction;
import br.com.fiap.mssales.functions.PurchaseHistoryFunction;
import br.com.fiap.mssales.repository.PurchaseRepository;
import br.com.fiap.mssales.utils.PurchaseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SalesServiceUnitTest {

	private SalesService salesService;

	@Mock
	private PurchaseRepository purchaseRepository;

	@Mock
	private CustomerFunction customerFunction;

	@Mock
	private ProductFunction productFunction;

	@Mock
	private PurchaseHistoryFunction purchaseHistoryFunction;

	@Mock
	private RestTemplate restTemplate;

	private ObjectMapper objectMapper;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		salesService = new SalesService(
				purchaseRepository,
				customerFunction,
				productFunction,
				purchaseHistoryFunction,
				restTemplate,
				objectMapper
		);
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Nested
	class GetPurchaseById{
		@Test
		void allowGetPurchaseById() {
			//Arrange
			Long id = 1L;
			Purchase purchase = PurchaseUtils.createFakePurchase(id);
			when(purchaseRepository.findById(any(Long.class))).thenReturn(Optional.of(purchase));

			//Act
			Purchase findPurchase = salesService.getPurchaseById(id);

			//Assert
			assertThat(findPurchase).isNotNull().isEqualTo(purchase);
			verify(purchaseRepository, times(1)).findById(any(Long.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenGetPurchaseById() {
			//Arrange
			Long id = 9999999L;
			when(purchaseRepository.findById(any(Long.class))).thenReturn(Optional.empty());

			//Act && Assert
			assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> salesService.getPurchaseById(id));
			verify(purchaseRepository, times(1)).findById(any(Long.class));
		}
	}

	@Nested
	class GetPurchaseByStatus{
		@Test
		void allowGetPurchasesByStatus() {
			//Arrange
			String status = "AGUARDANDO PAGAMENTO";
			Purchase purchase1 = PurchaseUtils.createFakePurchase(1L);
			Purchase purchase2 = PurchaseUtils.createFakePurchase(2L);
			List<Purchase> purchaseList = List.of(purchase1, purchase2);
			when(purchaseRepository.findByStatusIgnoreCase(any(String.class))).thenReturn(purchaseList);

			//Act
			List<Purchase> purchases = salesService.getPurchasesByStatus(status);

			//Assert
			assertThat(purchases).isNotNull().isEqualTo(purchaseList);
			verify(purchaseRepository, times(1)).findByStatusIgnoreCase(any(String.class));
		}
	}

	@Nested
	class CreatePurchase{
		/*@Test
		void allowCreatePurchase() throws Exception {
			//Arrange
			Customer customer = PurchaseUtils.createFakeCustomer();
			Product product = PurchaseUtils.createFakeProduct();
			PurchaseItem purchaseItem = new PurchaseItem();
			purchaseItem.setId(product.getId());
			purchaseItem.setQuantity(1);

			Purchase purchase = PurchaseUtils.createFakePurchase(1L);
			purchase.setCustomerCpf(customer.getCpf());
			purchase.setItems(List.of(purchaseItem));

			ResponseEntity<Product> responseEntity = new ResponseEntity<>(product, HttpStatus.OK);

			when(purchaseRepository.save(any(Purchase.class))).thenAnswer(p -> p.getArgument(0));
			when(customerFunction.findCustomer(any(String.class))).thenReturn(PurchaseUtils.createFakeCustomer());
			//when(restTemplate.getForEntity(any(String.class), String.class))
			//		.thenReturn(new ResponseEntity<>(PurchaseUtils.asJsonString(product), HttpStatus.OK));

			doNothing().when(purchaseHistoryFunction).saveHistory(any(PurchaseHistory.class));
			doNothing().when(productFunction).removeStock(any(Product.class));


			//Act
			Purchase savedPurchase = salesService.createPurchase(purchase);

			//Assert
			assertThat(savedPurchase.getStatus()).isEqualTo("AGUARDANDO PAGAMENTO");
			verify(purchaseRepository, times(1)).save(any(Purchase.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenCreatePurchase_Customer(){
			//Arrange
			Purchase purchase = PurchaseUtils.createFakePurchase(1L);
			when(customerFunction.findCustomer(any(String.class))).thenReturn(null);

			//Act && Assert
			assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> salesService.createPurchase(purchase));
			verify(purchaseRepository, never()).save(any(Purchase.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenCreatePurchase_Product() throws Exception {
			//Arrange
			Customer customer = PurchaseUtils.createFakeCustomer();
			Product product = PurchaseUtils.createFakeProduct();
			PurchaseItem purchaseItem = new PurchaseItem();
			purchaseItem.setId(product.getId());
			purchaseItem.setQuantity(1);

			Purchase purchase = PurchaseUtils.createFakePurchase(1L);
			purchase.setCustomerCpf(customer.getCpf());
			purchase.setItems(List.of(purchaseItem));

			when(customerFunction.findCustomer(any(String.class))).thenReturn(PurchaseUtils.createFakeCustomer());
			when(restTemplate.getForEntity(
					"http://localhost:8082/stock/getProductById/{id}",
					String.class,
					product.getId())).thenReturn(ResponseEntity.notFound().build());

			//Act && Assert
			assertThatExceptionOfType(HttpClientErrorException.class)
				.isThrownBy(() -> salesService.createPurchase(purchase));
			verify(purchaseRepository, never()).save(any(Purchase.class));

		}*/
	}

	@Nested
	class PayPurchase{
		@Test
		void allowPayPurchase(){
			//Arrange
			Long id = 1L;
			Purchase purchase = PurchaseUtils.createFakePurchase(id);
			when(purchaseRepository.findById(any(Long.class))).thenReturn(Optional.of(purchase));
			when(purchaseRepository.save(any(Purchase.class))).thenAnswer(p -> p.getArgument(0));

			//Act
			Purchase paidPurchase = salesService.payPurchase(id);

			//Assert
			assertThat(paidPurchase.getStatus()).isEqualTo("PAGO");
			verify(purchaseRepository, times(1)).findById(any(Long.class));
			verify(purchaseRepository, times(1)).save(any(Purchase.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenPayPurchase(){
			//Arrange
			Long id = 9999999L;
			when(purchaseRepository.findById(any(Long.class))).thenReturn(Optional.empty());

			//Act && Assert
			assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> salesService.payPurchase(id));
			verify(purchaseRepository, times(1)).findById(any(Long.class));
			verify(purchaseRepository, never()).save(any(Purchase.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenPayPurchase(){
			//Arrange
			Long id = 1L;
			Purchase purchase = PurchaseUtils.createFakePurchase(id, "CANCELADO");
			when(purchaseRepository.findById(any(Long.class))).thenReturn(Optional.of(purchase));

			//Act && Assert
			assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> salesService.payPurchase(id));
			verify(purchaseRepository, times(1)).findById(any(Long.class));
			verify(purchaseRepository, never()).save(any(Purchase.class));
		}
	}

	@Nested
	class CancelPurchase{
		@Test
		void allowCancelPurchase(){
			//Arrange
			Long id = 1L;
			Purchase purchase = PurchaseUtils.createFakePurchase(id);
			when(purchaseRepository.findById(any(Long.class))).thenReturn(Optional.of(purchase));
			when(purchaseRepository.save(any(Purchase.class))).thenAnswer(p -> p.getArgument(0));

			//Act
			Purchase canceledPurchase = salesService.cancelPurchase(id);

			//Assert
			assertThat(canceledPurchase.getStatus()).isEqualTo("CANCELADO");
			verify(purchaseRepository, times(1)).findById(any(Long.class));
			verify(purchaseRepository, times(1)).save(any(Purchase.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenCancelPurchase(){
			//Arrange
			Long id = 9999999L;
			when(purchaseRepository.findById(any(Long.class))).thenReturn(Optional.empty());

			//Act && Assert
			assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> salesService.cancelPurchase(id));
			verify(purchaseRepository, times(1)).findById(any(Long.class));
			verify(purchaseRepository, never()).save(any(Purchase.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenCancelPurchase(){
			//Arrange
			Long id = 1L;
			Purchase purchase = PurchaseUtils.createFakePurchase(id, "PAGO");
			when(purchaseRepository.findById(any(Long.class))).thenReturn(Optional.of(purchase));

			//Act && Assert
			assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> salesService.cancelPurchase(id));
			verify(purchaseRepository, times(1)).findById(any(Long.class));
			verify(purchaseRepository, never()).save(any(Purchase.class));
		}
	}
}
