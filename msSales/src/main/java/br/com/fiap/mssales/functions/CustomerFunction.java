package br.com.fiap.mssales.functions;

import br.com.fiap.mssales.entity.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "customer", url = "http://mscustomers:8081/api")
public interface CustomerFunction {

	@GetMapping("/consumer-findCustomer/{cpf}")
	Customer findCustomer(@PathVariable String cpf);

}
