package br.com.fiap.msproducts.service;

import br.com.fiap.msproducts.entity.Product;
import br.com.fiap.msproducts.utils.ProductUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@Rollback
public class StockServiceIntegTest {
	@Autowired
	private StockService stockService;

	@Nested
	class GetProductById {
		@Test
		void allowGetById() {
			//Arrange
			Product product = ProductUtils.createFakeProduct("Dummy test name 1");

			//Act
			Product savedProduct = stockService.createProduct(product);
			Product findProduct = stockService.getProductById(savedProduct.getId());

			//Assert
			assertThat(findProduct).isNotNull().isEqualTo(savedProduct);
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenGetById() {
			//Arrange
			Long id = 1000000L;

			// Act && Assert
			assertThatThrownBy(() -> stockService.getProductById(id))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(StockService.ENTITY_NOT_FOUND);
		}
	}
	@Nested
	class GetProductsByName {
		@Test
		void allowGetByName() {
			//Arrange
			String name = "Dummy test name";
			Product product1 = ProductUtils.createFakeProduct(name + " 2");
			Product product2 = ProductUtils.createFakeProduct(name + " 3");

			//Act
			Product createdProduct1 = stockService.createProduct(product1);
			Product createdProduct2 = stockService.createProduct(product2);
			List<Product> productList = List.of(createdProduct1, createdProduct2);

			List<Product> products = stockService.getProductsByName(name);

			//Assert
			assertThat(products).isNotNull().isEqualTo(productList);
		}
	}

	@Nested
	class CreateProduct {
		@Test
		void allowCreateProduct(){
			//Arrange
			Product product = ProductUtils.createFakeProduct("Dummy test name 4");

			//Act
			Product savedProduct = stockService.createProduct(product);

			//Assert
			assertThat(savedProduct).isNotNull().isEqualTo(product);
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenCreateProduct() {
			//Arrange
			Product product = ProductUtils.createFakeProduct("Dummy test name 5");

			// Act
			stockService.createProduct(product);

			//Assert
			assertThatThrownBy(() -> stockService.createProduct(product))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(StockService.PRODUCT_ALREADY_EXISTS);
		}
	}

	@Nested
	class UpdateProduct {
		@Test
		void allowUpdateProduct(){
			//Arrange
			Product product = ProductUtils.createFakeProduct("Dummy test name 6");
			Product oldProduct = ProductUtils.createFakeProduct("Dummy test name 6");
			Product newProduct = ProductUtils.createFakeProduct("Dummy test product name 6");
			newProduct.setPrice(20.0f);

			//Act
			product = stockService.createProduct(product);
			Product updatedProduct = stockService.updateProduct(product.getId(), newProduct);

			//Assert
			assertThat(updatedProduct).isNotNull().isNotEqualTo(oldProduct);
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenUpdateProduct() {
			//Arrange
			Product product = ProductUtils.createFakeProduct(1000007L, "Dummy test name 7");
			Product anotherProduct = ProductUtils.createFakeProduct("Dummy test name 7 updated");

			//Act && Assert
			assertThatThrownBy(() -> stockService.updateProduct(product.getId(), anotherProduct))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(StockService.ENTITY_NOT_FOUND);
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenUpdateProduct() {
			//Arrange
			Product oldProduct = ProductUtils.createFakeProduct("Dummy test name 8");
			Product product = ProductUtils.createFakeProduct("Dummy test name 9");
			Product updateProduct = ProductUtils.createFakeProduct("Dummy test name 8");


			// Act
			stockService.createProduct(oldProduct);
			stockService.createProduct(product);

			//Assert
			assertThatThrownBy(() -> stockService.updateProduct(product.getId(), updateProduct))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(StockService.PRODUCT_ALREADY_EXISTS);
		}

	}

	@Nested
	class DeleteProduct {
		@Test
		void allowDeleteProduct(){
			//Arrange
			Product product = ProductUtils.createFakeProduct("Dummy test name 10");
			product.setQuantity(0); //setting 0 to be deleted

			//Act
			Product savedProduct = stockService.createProduct(product);
			String message = stockService.deleteProduct(savedProduct.getId());


			//Assert
			assertThat(message).isNotNull().isEqualTo(String.format(StockService.PRODUCT_DELETED, savedProduct.getName()));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenDeleteProduct() {
			//Arrange
			Product product = ProductUtils.createFakeProduct(1000011L,"Dummy test name 11");

			//Act && Assert
			assertThatThrownBy(() -> stockService.deleteProduct(product.getId()))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(StockService.ENTITY_NOT_FOUND);
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenDeleteProduct() {
			//Arrange
			Product product = ProductUtils.createFakeProduct( "Dummy test name 12");

			//Act
			Product savedProduct = stockService.createProduct(product);

			//Assert
			assertThatThrownBy(() -> stockService.deleteProduct(savedProduct.getId()))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(StockService.PRODUCT_HAS_STOCK);
		}

	}
	@Nested
	class RemoveStock{
		@Test
		void allowRemoveStock(){
			//Arrange
			Product product = ProductUtils.createFakeProduct("Dummy test name 13");

			//Act
			Product savedProduct = stockService.createProduct(product);
			stockService.removeStock(savedProduct);
			savedProduct = stockService.getProductById(savedProduct.getId());

			//Assert
			assertThat(savedProduct.getQuantity()).isZero();
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenRemoveStock(){
			//Arrange
			Product product = ProductUtils.createFakeProduct(1000014L, "Dummy test name 14");

			//Act && Assert
			assertThatThrownBy(() -> stockService.removeStock(product))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(StockService.ENTITY_NOT_FOUND);
		}
	}
}
