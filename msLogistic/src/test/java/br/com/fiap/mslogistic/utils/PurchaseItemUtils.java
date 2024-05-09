package br.com.fiap.mslogistic.utils;

import br.com.fiap.mslogistic.entity.PurchaseItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PurchaseItemUtils {
	public static PurchaseItem createFakePurchaseItem() {
		PurchaseItem purchaseItem = new PurchaseItem();

		purchaseItem.setProductId(1L);
		purchaseItem.setQuantity(1);

		return purchaseItem;
	}

	public static PurchaseItem createFakePurchaseItem(Long productId, Integer quantity) {
		PurchaseItem purchaseItem = new PurchaseItem();

		purchaseItem.setProductId(productId);
		purchaseItem.setQuantity(quantity);

		return purchaseItem;
	}


	public static String asJsonString(final Object object) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(object);
	}
}
