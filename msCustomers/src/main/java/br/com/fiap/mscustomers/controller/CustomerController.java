package br.com.fiap.mscustomers.controller;

import br.com.fiap.mscustomers.entity.Customer;
import br.com.fiap.mscustomers.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@GetMapping("/getCustomerByCpf/{cpf}")
	public ResponseEntity<Customer> getByCpf(@PathVariable String cpf) {
		return ResponseEntity.ok(this.customerService.getByCpf(cpf));
	}

	@GetMapping("/getCustomersByName/{name}")
	public ResponseEntity<List<Customer>> getByName(@PathVariable String name) {
		return ResponseEntity.ok(this.customerService.getByName(name));
	}

	@PostMapping("/createCustomer")
	public ResponseEntity<Customer> createCustomer(@RequestBody @Valid Customer customer) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.customerService.createCustomer(customer));
	}

	@PutMapping("/updateCustomer/{cpf}")
	public ResponseEntity<Customer> updateCustomer(@PathVariable String cpf, @RequestBody @Valid Customer customer) {
		return ResponseEntity.ok(this.customerService.updateCustomer(cpf, customer));
	}

	@DeleteMapping("/deleteCustomer/{cpf}")
	public ResponseEntity<String> deleteCustomer(@PathVariable String cpf) {
		return ResponseEntity.ok(this.customerService.deleteCustomer(cpf));
	}
}
