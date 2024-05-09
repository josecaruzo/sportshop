package br.com.fiap.msstock.service;

import br.com.fiap.msstock.entity.Product;
import br.com.fiap.msstock.repository.ProductRepository;
import br.com.fiap.msstock.utils.ProductUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;


public class StockServiceUnitTest {
	private StockService stockService;

	@Mock
	private ProductRepository productRepository;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		stockService = new StockService(productRepository);
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}
	
	@Nested
	class GetProductById {
		@Test
		void allowGetById() {
			//Arrange
			Product product = ProductUtils.createFakeProduct();
			when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

			//Act
			Product findProduct = stockService.getProductById(product.getId());

			//Assert
			assertThat(findProduct).isNotNull().isEqualTo(product);
			verify(productRepository, times(1)).findById(any(Long.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenGetById() {
			//Arrange
			Product product = ProductUtils.createFakeProduct();
			when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

			//Act && Assert
			assertThatThrownBy(() -> stockService.getProductById(product.getId()))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessageContaining(StockService.ENTITY_NOT_FOUND);
			verify(productRepository, times(1)).findById(any(Long.class));
		}
	}
	@Nested
	class GetProductsByName {
		@Test
		void allowGetByName() {
			//Arrange
			String name = "Dummy test name";
			Product product1 = ProductUtils.createFakeProduct(1L, name + " 1");
			Product product2 = ProductUtils.createFakeProduct(2L, name + " 2");
			List<Product> productList = List.of(product1, product2);

			when(productRepository.findByNameContainingIgnoreCaseOrderById(any(String.class))).thenReturn(productList);

			//Act
			List<Product> products = stockService.getProductsByName(name);

			//Assert
			assertThat(products).isNotNull().isEqualTo(productList);
			verify(productRepository, times(1)).findByNameContainingIgnoreCaseOrderById(any(String.class));
		}
	}

	@Nested
	class CreateProduct {
		@Test
		void allowCreateProduct(){
			//Arrange
			Product product = ProductUtils.createFakeProduct();
			when(productRepository.save(any(Product.class))).thenAnswer(p -> p.getArgument(0));

			//Act
			Product savedProduct = stockService.createProduct(product);

			//Assert
			assertThat(savedProduct).isNotNull().isEqualTo(product);
			verify(productRepository, times(1)).save(any(Product.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenCreateProduct() {
			//Arrange
			Product product = ProductUtils.createFakeProduct();
			when(productRepository.findByNameEqualsIgnoreCase(any(String.class))).thenReturn(Optional.of(product));

			//Act && Assert
			assertThatThrownBy(() -> stockService.createProduct(product))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessageContaining(StockService.PRODUCT_ALREADY_EXISTS);
			verify(productRepository, times(1)).findByNameEqualsIgnoreCase(any(String.class));
			verify(productRepository, never()).save(any(Product.class));
		}
	}

	@Nested
	class UpdateProduct {
		@Test
		void allowUpdateProduct(){
			//Arrange
			Product product = ProductUtils.createFakeProduct();
			Product oldProduct = ProductUtils.createFakeProduct();
			Product newProduct = ProductUtils.createFakeProduct();
			newProduct.setPrice(20.0f);
			when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
			when(productRepository.save(any(Product.class))).thenAnswer(p -> p.getArgument(0));

			//Act
			Product savedProduct = stockService.updateProduct(oldProduct.getId(), newProduct);

			//Assert
			assertThat(savedProduct).isNotNull().isNotEqualTo(oldProduct).isEqualTo(newProduct);
			verify(productRepository, times(1)).findById(any(Long.class));
			verify(productRepository, times(1)).save(any(Product.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenUpdateProduct() {
			//Arrange
			Product product = ProductUtils.createFakeProduct();
			Product newProduct = ProductUtils.createFakeProduct();
			when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

			//Act && Assert
			assertThatThrownBy(() -> stockService.updateProduct(product.getId(), newProduct))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessageContaining(StockService.ENTITY_NOT_FOUND);
			verify(productRepository, times(1)).findById(any(Long.class));
			verify(productRepository, never()).save(any(Product.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenUpdateProduct() {
			//Arrange
			Product product = ProductUtils.createFakeProduct();
			Product newProduct = ProductUtils.createFakeProduct();
			when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
			when(productRepository.findByNameEqualsIgnoreCase(any(String.class))).thenReturn(Optional.of(newProduct));

			//Act && Assert
			assertThatThrownBy(() -> stockService.updateProduct(product.getId(), newProduct))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessageContaining(StockService.PRODUCT_ALREADY_EXISTS);
			verify(productRepository, times(1)).findById(any(Long.class));
			verify(productRepository, times(1)).findByNameEqualsIgnoreCase(any(String.class));
			verify(productRepository, never()).save(any(Product.class));
		}

	}

	@Nested
	class DeleteProduct {
		@Test
		void allowDeleteProduct(){
			//Arrange
			Product product = ProductUtils.createFakeProduct();
			product.setQuantity(0); // Zeroing stock
			when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

			//Act
			String message = stockService.deleteProduct(product.getId());

			//Assert
			assertThat(message).isNotNull().isEqualTo(String.format(StockService.PRODUCT_DELETED, product.getName()));
			verify(productRepository, times(1)).findById(any(Long.class));
			verify(productRepository, times(1)).delete(any(Product.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenDeleteProduct() {
			//Arrange
			Product product = ProductUtils.createFakeProduct();
			when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

			//Act && Assert
			assertThatThrownBy(() -> stockService.deleteProduct(product.getId()))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessageContaining(StockService.ENTITY_NOT_FOUND);
			verify(productRepository, times(1)).findById(any(Long.class));
			verify(productRepository, never()).delete(any(Product.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenDeleteProduct() {
			//Arrange
			Product product = ProductUtils.createFakeProduct();
			when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

			//Act && Assert
			assertThatThrownBy(() -> stockService.deleteProduct(product.getId()))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessageContaining(StockService.PRODUCT_HAS_STOCK);
			verify(productRepository, times(1)).findById(any(Long.class));
			verify(productRepository, never()).delete(any(Product.class));
		}

	}
	@Nested
	class RemoveStock{
		@Test
		void allowRemoveStock(){
			//Arrange
			Product product = ProductUtils.createFakeProduct();
			Product productToUpdate = ProductUtils.createFakeProduct();
			productToUpdate.setQuantity(productToUpdate.getQuantity() - product.getQuantity());

			when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(productToUpdate));
			when(productRepository.save(any(Product.class))).thenAnswer(p -> p.getArgument(0));

			//Act
			stockService.removeStock(product);

			//Assert
			verify(productRepository, times(1)).findById(any(Long.class));
			verify(productRepository, times(1)).save(any(Product.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenRemoveStock(){
			//Arrange
			Product product = ProductUtils.createFakeProduct();
			when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

			//Act && Assert
			assertThatThrownBy(() -> stockService.removeStock(product))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessageContaining(StockService.ENTITY_NOT_FOUND);
			verify(productRepository, times(1)).findById(any(Long.class));
			verify(productRepository, never()).save(any(Product.class));
		}
	}
}
