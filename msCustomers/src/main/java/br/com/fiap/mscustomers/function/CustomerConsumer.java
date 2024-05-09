package br.com.fiap.mscustomers.function;

import br.com.fiap.mscustomers.entity.Customer;
import br.com.fiap.mscustomers.service.CustomerService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CustomerConsumer {
	private final CustomerService customerService;

	public CustomerConsumer(CustomerService customerService) {
		this.customerService = customerService;
	}

	@Bean(name = "findCustomer")
	Function<String, Customer> findCustomerConsumer(){
		return customerService::getByCpf;
	}
}
