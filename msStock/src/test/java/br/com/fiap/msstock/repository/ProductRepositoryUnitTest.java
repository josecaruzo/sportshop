package br.com.fiap.msstock.repository;

import br.com.fiap.msstock.entity.Product;
import br.com.fiap.msstock.utils.ProductUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductRepositoryUnitTest {
	@Mock
	private ProductRepository productRepository;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}
	
	@Test
	void allowFindByNameEqualsIgnoreCase(){
		//Arrange
		Product product = ProductUtils.createFakeProduct();
		when(productRepository.findByNameEqualsIgnoreCase(any(String.class))).thenReturn(Optional.of(product));

		//Act
		Optional<Product> findProduct = productRepository.findByNameEqualsIgnoreCase(product.getName());

		//Assert
		assertThat(findProduct).isPresent().isEqualTo(Optional.of(product));
		verify(productRepository, times(1)).findByNameEqualsIgnoreCase(any(String.class));
	}

	@Test
	void allowFindByNameContainingIgnoreCase(){
		//Arrange
		String name = "Dummy test name";
		Product product1 = ProductUtils.createFakeProduct(1L,name + " 1");
		Product product2 = ProductUtils.createFakeProduct(2L, name + " 2");
		List<Product> productList = List.of(product1, product2);

		when(productRepository.findByNameContainingIgnoreCaseOrderById(any(String.class))).thenReturn(productList);

		//Act
		List<Product> findProduct = productRepository.findByNameContainingIgnoreCaseOrderById(name);

		//Assert
		assertThat(findProduct).isNotEmpty().isEqualTo(productList);
		verify(productRepository, times(1)).findByNameContainingIgnoreCaseOrderById(any(String.class));
	}

	@Test
	void allowCreateProduct(){
		//Arrange
		Product product = ProductUtils.createFakeProduct();
		when(productRepository.save(any(Product.class))).thenReturn(product);

		//Act
		Product savedProduct = productRepository.save(product);

		//Assert
		assertThat(savedProduct).isNotNull().isEqualTo(product);
		verify(productRepository, times(1)).save(any(Product.class));
	}

	@Test
	void allowUpdateProduct(){
		//Arrange
		Product product = ProductUtils.createFakeProduct();
		product.setPrice(20.0f);

		when(productRepository.save(any(Product.class))).thenReturn(product);

		//Act
		Product savedProduct = productRepository.save(product);

		//Assert
		assertThat(savedProduct).isNotNull().isEqualTo(product);
		verify(productRepository, times(1)).save(any(Product.class));
	}

	@Test
	void allowDeleteProduct(){
		//Arrange
		Product product = ProductUtils.createFakeProduct();
		doNothing().when(productRepository).delete(any(Product.class));

		//Act
		productRepository.delete(product);

		//Assert
		verify(productRepository, times(1)).delete(any(Product.class));
	}
}
