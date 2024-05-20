package br.com.fiap.msstock.repository;

import br.com.fiap.msstock.entity.Product;
import br.com.fiap.msstock.utils.ProductUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback //Rollback the transaction after each test
class ProductRepositoryIntegTest {

	@Autowired
	private ProductRepository productRepository;

	@Test
	void allowFindByNameEqualsIgnoreCase(){
		//Arrange
		Product product = ProductUtils.createFakeProduct();

		//Act
		Product createdProduct = productRepository.save(product);
		Optional<Product> findProduct = productRepository.findByNameEqualsIgnoreCase(createdProduct.getName());

		//Assert
		assertThat(findProduct).isPresent().isEqualTo(Optional.of(createdProduct));
	}

	@Test
	void allowFindByNameContainingIgnoreCase(){
		//Arrange
		String name = "Dummy test name";
		Product product1 = ProductUtils.createFakeProduct(1L, name + " 1");
		Product product2 = ProductUtils.createFakeProduct(2L, name + " 2");

		//Act
		Product createdProduct1 = productRepository.save(product1);
		Product createdProduct2 = productRepository.save(product2);
		List<Product> productList = List.of(createdProduct1, createdProduct2);

		List<Product> findProduct = productRepository.findByNameContainingIgnoreCase(name);

		//Assert
		assertThat(findProduct).isNotEmpty().hasSize(productList.size());
	}

	@Test
	void allowCreateProduct(){
		//Arrange
		Product product = ProductUtils.createFakeProduct();

		//Act
		Product savedProduct = productRepository.save(product);

		//Assert
		assertThat(savedProduct).isNotNull().isInstanceOf(Product.class);
		assertThat(savedProduct.getId()).isNotNull().isPositive();
		assertThat(savedProduct.getName()).isEqualTo(product.getName());
	}

	@Test
	void allowUpdateProduct(){
		//Arrange
		Product product = ProductUtils.createFakeProduct();

		//Act
		Product savedProduct = productRepository.save(product);
		savedProduct.setPrice(20.0f);
		Product updatedProduct = productRepository.save(savedProduct);

		//Assert
		assertThat(updatedProduct).isNotNull().isEqualTo(savedProduct);
	}

	@Test
	void allowDeleteProduct(){
		//Arrange
		Product product = ProductUtils.createFakeProduct();

		//Act
		Product savedProduct = productRepository.save(product);
		productRepository.delete(savedProduct);
		Optional<Product> findProduct = productRepository.findById(savedProduct.getId());

		//Assert
		assertThat(findProduct).isNotPresent();
	}
}
