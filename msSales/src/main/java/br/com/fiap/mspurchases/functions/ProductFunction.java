package br.com.fiap.mspurchases.functions;

import br.com.fiap.mspurchases.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "product", url = "http://msstock:8082/api")
public interface ProductFunction {

	@PostMapping("/consumer-removeStock")
	void removeStock(Product product);
}
