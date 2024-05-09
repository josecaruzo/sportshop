package br.com.fiap.msbatches.services;

import br.com.fiap.msbatches.entity.Product;
import br.com.fiap.msbatches.repository.ProductRepository;
import org.springframework.batch.item.ItemProcessor;

public class ProductProcessor implements ItemProcessor<Product, Product> {
	private final ProductRepository productRepository;

	public ProductProcessor(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public Product process(Product item) throws Exception {
		Float price = (float) (Math.round(item.getPrice() * 100.0) / 100.0);
		item.setPrice(price);

		Product product = productRepository.findByName(item.getName()).orElse(new Product());

		if(product.getId() == null) {
			product.setName(item.getName());
			product.setDescription(item.getDescription());
			product.setPrice(item.getPrice());
			product.setQuantity(item.getQuantity());

			var savedProduct = productRepository.save(product);
			item.setId(savedProduct.getId());
		}
		else{
			item.setId(product.getId());
		}

		System.out.println("Processing: " + item.toString());
		return item;
	}
}
