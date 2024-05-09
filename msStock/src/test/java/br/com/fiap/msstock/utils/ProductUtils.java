package br.com.fiap.msstock.utils;

import br.com.fiap.msstock.entity.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductUtils {

	public static Product createFakeProduct() {
		Product product = new Product();
		product.setId(1000000L);
		product.setName("Dummy product");
		product.setDescription("Dummy product description");
		product.setPrice(15.80f);
		product.setQuantity(100);

		return product;
	}

	public static Product createFakeProduct(Long id, String name) {
		Product product = new Product();
		product.setId(id);
		product.setName(name);
		product.setDescription("Dummy product description");
		product.setPrice(15.80f);
		product.setQuantity(100);

		return product;
	}

	public static Product createFakeProduct(String name) {
		Product product = new Product();
		product.setName(name);
		product.setDescription("Dummy product description");
		product.setPrice(15.80f);
		product.setQuantity(100);

		return product;
	}

	public static String asJsonString(final Object object) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(object);
	}
}
