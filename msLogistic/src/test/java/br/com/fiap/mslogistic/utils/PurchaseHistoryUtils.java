package br.com.fiap.mslogistic.utils;

import br.com.fiap.mslogistic.entity.PurchaseHistory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

public class PurchaseHistoryUtils {
	public static PurchaseHistory createFakePurchaseHistory() {
		PurchaseHistory purchaseHistory = new PurchaseHistory();
		purchaseHistory.setStatusDate(LocalDateTime.now());

		return purchaseHistory;
	}

	public static PurchaseHistory createFakePurchaseHistory(Long purchaseId, String status) {
		PurchaseHistory purchaseHistory = new PurchaseHistory();

		purchaseHistory.setPurchaseId(purchaseId);
		purchaseHistory.setStatus(status);
		purchaseHistory.setStatusDate(LocalDateTime.now());

		return purchaseHistory;
	}

	public static PurchaseHistory createFakePurchaseHistory(Long id, Long purchaseId, String status) {
		PurchaseHistory purchaseHistory = new PurchaseHistory();

		purchaseHistory.setId(id);
		purchaseHistory.setPurchaseId(purchaseId);
		purchaseHistory.setStatus(status);
		purchaseHistory.setStatusDate(LocalDateTime.now());

		return purchaseHistory;
	}

	public static String asJsonString(final Object object) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(object);
	}
}
