package br.com.fiap.msstock.service;

import br.com.fiap.msstock.entity.Product;
import br.com.fiap.msstock.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {
	public static final String ENTITY_NOT_FOUND = "Produto não encontrado"; //Product not found
	public static final String PRODUCT_ALREADY_EXISTS = "Produto já cadastrado com esse nome"; //Product already registered
	public static final String PRODUCT_HAS_STOCK = "O produto tem quantidade em estoque, ele não pode ser deletado."; //The product has stock, it can't be deleted
	public static final String PRODUCT_DELETED = "Produto %s deletado com sucesso"; //Product %s deleted successfully

	private final ProductRepository productRepository;

	public StockService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public Product getProductById(Long id) {
		return this.productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
	}

	public List<Product> getProductsByName(String name) {
		return this.productRepository.findByNameContainingIgnoreCase(name);
	}

	public Product createProduct(Product product) {
		//Before creating, check if the name already exists
		validateIfNameExists(product.getName());
		return this.productRepository.save(product);
	}

	public Product updateProduct(Long id, Product product){
		Product productToUpdate = this.productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

		//Before updating, check if the name already exists
		validateIfNameExists(product.getName());

		productToUpdate.setName(product.getName());
		productToUpdate.setDescription(product.getDescription());
		productToUpdate.setPrice(product.getPrice());
		productToUpdate.setQuantity(product.getQuantity());

		return this.productRepository.save(productToUpdate);
	}

	public String deleteProduct(Long id){
		Product productToDelete = this.productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

		//If the product has stock, it can't be deleted
		if(productToDelete.getQuantity() > 0){
			throw new DataIntegrityViolationException(PRODUCT_HAS_STOCK);
		}

		this.productRepository.delete(productToDelete);
		return String.format(PRODUCT_DELETED, productToDelete.getName());
	}

	public void updateStock(Product product) {
			Product productToUpdate = this.productRepository.findById(product.getId())
					.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

			//The stock was validated before in the consumer
			productToUpdate.setQuantity(productToUpdate.getQuantity() + product.getQuantity());
			this.productRepository.save(productToUpdate);
	}

	private void validateIfNameExists(String name) {
		Product findProduct = this.productRepository.findByNameEqualsIgnoreCase(name).orElse(null);

		if (findProduct != null) {
			throw new DataIntegrityViolationException(PRODUCT_ALREADY_EXISTS);
		}
	}
}
