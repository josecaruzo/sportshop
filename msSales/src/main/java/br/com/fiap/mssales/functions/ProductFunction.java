package br.com.fiap.mssales.functions;

import br.com.fiap.mssales.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "product", url = "http://msstock:8082/api")
public interface ProductFunction {

	@PostMapping("/updateStock")
	void updateStock(Product product);

	@GetMapping("/findProduct/{id}")
	Product findProduct(@PathVariable Long id);
}
