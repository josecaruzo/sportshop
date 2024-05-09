package br.com.fiap.msproducts.controller;

import br.com.fiap.msproducts.controller.exception.ControllerExceptionHandler;
import br.com.fiap.msproducts.entity.Product;
import br.com.fiap.msproducts.service.StockService;
import br.com.fiap.msproducts.utils.ProductUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StockControllerUnitTest {
	private MockMvc mockMvc;

	@Mock
	private StockService stockService;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		StockController stockController = new StockController(stockService);
		mockMvc = MockMvcBuilders.standaloneSetup(stockController)
				.setControllerAdvice(new ControllerExceptionHandler())
				.build();
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Nested
	class GetProductById {
		@Test
		void allowGetById() throws Exception {
			//Arrange
			Product product  = ProductUtils.createFakeProduct(10000001L, "Dummy test name 1");
			when(stockService.getProductById(any(Long.class))).thenReturn(product);

			//Act && Assert
			mockMvc.perform(get("/stock/getProductById/{id}", product.getId())
					).andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.content().json(ProductUtils.asJsonString(product)));
			verify(stockService, times(1)).getProductById(any(Long.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenGetById() throws Exception{
			//Arrange
			Long id = 1000000L;
			when(stockService.getProductById(any(Long.class))).thenThrow(new EntityNotFoundException(StockService.ENTITY_NOT_FOUND));

			// Act && Assert
			mockMvc.perform(get("/stock/getProductById/{id}", id)
			).andExpect(status().isNotFound());
			verify(stockService, times(1)).getProductById(any(Long.class));
		}
	}
	@Nested
	class GetProductsByName {
		@Test
		void allowGetByName() throws Exception {
			//Arrange
			String name = "Dummy test name";
			Product product1 = ProductUtils.createFakeProduct(10000002L, name + " 2");
			Product product2 = ProductUtils.createFakeProduct(10000003L, name + " 3");

			List<Product> productList = List.of(product1, product2);

			when(stockService.getProductsByName(any(String.class))).thenReturn(productList);

			//Act && Assert
			mockMvc.perform(get("/stock/getProductsByName/{name}", name)
					).andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.content().json(ProductUtils.asJsonString(productList)));
			verify(stockService, times(1)).getProductsByName(any(String.class));
		}
	}

	@Nested
	class CreateProduct {
		@Test
		void allowCreateProduct() throws Exception{
			//Arrange
			Product product = ProductUtils.createFakeProduct(10000004L, "Dummy test name 4");
			when(stockService.createProduct(any(Product.class))).thenAnswer(p -> p.getArgument(0));

			//Act && Assert
			mockMvc.perform(post("/stock/createProduct")
					.contentType("application/json")
					.content(ProductUtils.asJsonString(product))
			).andExpect(status().isCreated())
					.andExpect(MockMvcResultMatchers.content().json(ProductUtils.asJsonString(product)));
			verify(stockService, times(1)).createProduct(any(Product.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenCreateProduct() throws Exception{
			//Arrange
			Product product = ProductUtils.createFakeProduct(10000005L, "Dummy test name 5");
			when(stockService.createProduct(any(Product.class))).thenThrow(DataIntegrityViolationException.class);

			//Act && Assert
			mockMvc.perform(post("/stock/createProduct")
					.contentType(MediaType.APPLICATION_JSON)
					.content(ProductUtils.asJsonString(product))
			).andExpect(status().isBadRequest());
			verify(stockService, times(1)).createProduct(any(Product.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenCreateProduct() throws Exception{
			//Arrange
			Product product = ProductUtils.createFakeProduct(10000006L, "Dummy test name 6");
			product.setName(""); // without name
			product.setDescription(""); // without description
			product.setQuantity(-1); //wrong quantity

			//Act && Assert
			mockMvc.perform(post("/stock/createProduct")
					.contentType(MediaType.APPLICATION_JSON)
					.content(ProductUtils.asJsonString(product))
			).andExpect(status().isBadRequest());
			verify(stockService, never()).createProduct(any(Product.class));
		}
	}

	@Nested
	class UpdateProduct {
		@Test
		void allowUpdateProduct() throws Exception{
			//Arrange
			Product product = ProductUtils.createFakeProduct(10000007L, "Dummy test name 7");
			Product newProduct = ProductUtils.createFakeProduct(10000007L, "Dummy test product name 7 updated");

			when(stockService.updateProduct(any(Long.class), any(Product.class))).thenAnswer(p -> p.getArgument(1));

			//Act && Assert
			mockMvc.perform(put("/stock/updateProduct/{id}", product.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.content(ProductUtils.asJsonString(newProduct))
			).andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.content().json(ProductUtils.asJsonString(newProduct)));
			verify(stockService, times(1)).updateProduct(any(Long.class), any(Product.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenUpdateProduct() throws Exception{
			//Arrange
			Product product = ProductUtils.createFakeProduct(10000008L, "Dummy test name 8");
			Product newProduct = ProductUtils.createFakeProduct(10000008L, "Dummy test name 8 updated");

			when(stockService.updateProduct(any(Long.class), any(Product.class))).thenThrow(new EntityNotFoundException(StockService.ENTITY_NOT_FOUND));

			//Act && Assert
			mockMvc.perform(put("/stock/updateProduct/{id}", product.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.content(ProductUtils.asJsonString(newProduct))
			).andExpect(status().isNotFound());
			verify(stockService, times(1)).updateProduct(any(Long.class), any(Product.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenUpdateProduct() throws Exception{
			//Arrange
			Product product = ProductUtils.createFakeProduct(10000009L, "Dummy test name 9");
			Product newProduct = ProductUtils.createFakeProduct(10000009L, "Dummy test name 1");

			when(stockService.updateProduct(any(Long.class), any(Product.class))).thenThrow(DataIntegrityViolationException.class);

			//Act && Assert
			mockMvc.perform(put("/stock/updateProduct/{id}", product.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.content(ProductUtils.asJsonString(newProduct))
			).andExpect(status().isBadRequest());
			verify(stockService, times(1)).updateProduct(any(Long.class), any(Product.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenUpdateProduct() throws Exception{
			//Arrange
			Product product = ProductUtils.createFakeProduct(10000010L, "Dummy test name 10");
			Product newProduct = ProductUtils.createFakeProduct(10000010L, "Dummy test name 10");
			newProduct.setName(""); // without name
			newProduct.setDescription(""); // without description
			newProduct.setQuantity(-1); //wrong quantity

			//Act && Assert
			mockMvc.perform(put("/stock/updateProduct/{id}", product.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.content(ProductUtils.asJsonString(newProduct))
			).andExpect(status().isBadRequest());
			verify(stockService, never()).updateProduct(any(Long.class), any(Product.class));
		}

	}

	@Nested
	class DeleteProduct {
		@Test
		void allowDeleteProduct() throws Exception{
			//Arrange
			Product product = ProductUtils.createFakeProduct(10000011L, "Dummy test name 11");
			product.setQuantity(0); // Zeroing stock
			when(stockService.deleteProduct(any(Long.class))).thenReturn(String.format(StockService.PRODUCT_DELETED, product.getName()));

			//Act && Assert
			mockMvc.perform(delete("/stock/deleteProduct/{id}", product.getId())
			).andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.content().string(String.format(StockService.PRODUCT_DELETED, product.getName())));
			verify(stockService, times(1)).deleteProduct(any(Long.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenDeleteProduct() throws Exception{
			//Arrange
			Product product = ProductUtils.createFakeProduct(10000012L, "Dummy test name 12");
			when(stockService.deleteProduct(any(Long.class))).thenThrow(new EntityNotFoundException(StockService.ENTITY_NOT_FOUND));

			//Act && Assert
			mockMvc.perform(delete("/stock/deleteProduct/{id}", product.getId())
			).andExpect(status().isNotFound());
			verify(stockService, times(1)).deleteProduct(any(Long.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenDeleteProduct() throws Exception{
			//Arrange
			Product product = ProductUtils.createFakeProduct(10000013L, "Dummy test name 13");
			when(stockService.deleteProduct(any(Long.class))).thenThrow(new DataIntegrityViolationException(StockService.PRODUCT_HAS_STOCK));

			//Act && Assert
			mockMvc.perform(delete("/stock/deleteProduct/{id}", product.getId())
			).andExpect(status().isBadRequest());
			verify(stockService, times(1)).deleteProduct(any(Long.class));
		}
	}

}
