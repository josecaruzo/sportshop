package br.com.fiap.mslogistic.service;

import br.com.fiap.mslogistic.entity.Purchase;
import br.com.fiap.mslogistic.entity.PurchaseHistory;
import br.com.fiap.mslogistic.repository.PurchaseHistoryRepository;
import br.com.fiap.mslogistic.repository.PurchaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LogisticService {
	public static final String HISTORY_NOT_FOUND = "Histórico de compra não encontrado"; //History not found
	public static final String PURCHASE_NOT_FOUND = "Pedido não encontrado"; //Purchase not found
	public static final String NOT_POSSIBLE_TO_DELIVER = "Não é possível entregar pedido com o status: %s"; //Not possible to deliver purchase
	public static final String PURCHASE_DELIVERED = "Pedido %s entregue";

	private static final String WAITING_DELIVERY_STATUS = "AGUARDANDO ENTREGA";
	private static final String PAID_STATUS = "PAGO";
	private static final String DELIVERED_STATUS = "ENTREGUE";

	private final PurchaseRepository purchaseRepository;
	private final PurchaseHistoryRepository purchaseHistoryRepository;

	public LogisticService(PurchaseRepository purchaseRepository, PurchaseHistoryRepository purchaseHistoryRepository) {
		this.purchaseRepository = purchaseRepository;
		this.purchaseHistoryRepository = purchaseHistoryRepository;
	}

	public List<PurchaseHistory> getHistoryByPurchaseId(Long purchaseId) {
		List<PurchaseHistory> history = this.purchaseHistoryRepository.findByPurchaseId(purchaseId);
		if (history.isEmpty()) throw new EntityNotFoundException(HISTORY_NOT_FOUND);

		return history;
	}

	public PurchaseHistory createPurchaseHistory(PurchaseHistory purchaseHistory) {
		return this.purchaseHistoryRepository.save(purchaseHistory);
	}

	public List<Purchase> dispatchPurchases() {
		List<Purchase> purchases = purchaseRepository.findByStatusOrderByDeliveryZipCode(PAID_STATUS);

		if (purchases.isEmpty()) return purchases;

		String deliveryGroupId = createNewDeliveryGroup();
		String groupId = purchases.get(0).getDeliveryZipCode().substring(0, 4);

		for (Purchase purchase : purchases) {
			String purchaseGroupId = purchase.getDeliveryZipCode().substring(0, 4);

			if (!purchaseGroupId.equals(groupId)) {
				deliveryGroupId = createNewDeliveryGroup();
				groupId = purchaseGroupId;
			}

			purchase.setDeliveryGroup(deliveryGroupId);
			purchase.setStatus(WAITING_DELIVERY_STATUS);
			purchase = this.purchaseRepository.save(purchase);

			PurchaseHistory history = new PurchaseHistory();
			history.setPurchaseId(purchase.getId());
			history.setStatus(WAITING_DELIVERY_STATUS);
			history.setStatusDate(LocalDateTime.now());
			history = this.purchaseHistoryRepository.save(history);
		}
		return purchases;
	}

	public String deliveryPurchase(Long purchaseId) {
		Purchase purchase = this.purchaseRepository.findById(purchaseId)
				.orElseThrow(() -> new EntityNotFoundException(PURCHASE_NOT_FOUND));

		if(purchase.getStatus().equals(WAITING_DELIVERY_STATUS)){
			purchase.setStatus(DELIVERED_STATUS);
			this.purchaseRepository.save(purchase);

			PurchaseHistory newHistory = new PurchaseHistory();
			newHistory.setPurchaseId(purchaseId);
			newHistory.setStatus(DELIVERED_STATUS);
			newHistory.setStatusDate(LocalDateTime.now());
			this.purchaseHistoryRepository.save(newHistory);
			return String.format(PURCHASE_DELIVERED, purchaseId);
		}
		else {
			throw new DataIntegrityViolationException(String.format(NOT_POSSIBLE_TO_DELIVER, purchase.getStatus()));
		}
	}

	private String createNewDeliveryGroup() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		LocalDateTime now = LocalDateTime.now();
		return now.format(formatter);
	}
}
