package br.com.fiap.mscustomers.service;

import br.com.fiap.mscustomers.entity.Customer;
import br.com.fiap.mscustomers.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.HibernateException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class CustomerService {
	public static final String ENTITY_NOT_FOUND = "Cliente não encontrado"; //Customer not found
	public static final String CPF_ALREADY_EXISTS = "CPF já cadastrado"; //CPF already registered
	public static final String CUSTOMER_DELETED = "Cliente %s : %s deletado com sucesso";

	private final CustomerRepository customerRepository;

	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	public Customer getByCpf(String cpf) {
		return this.customerRepository.findById(cpf)
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
	}

	public List<Customer> getByName(String name) {
		return this.customerRepository.findByFullNameContainingIgnoreCase(name);
	}

	public Customer createCustomer(Customer customer) {
		Customer findCustomer = this.customerRepository.findById(customer.getCpf()).orElse(null);
		if (findCustomer != null) throw new DataIntegrityViolationException(CPF_ALREADY_EXISTS);

		return this.customerRepository.save(customer);
	}

	public Customer updateCustomer(String cpf, Customer customer) {
		Customer customerToUpdate = this.customerRepository.findById(cpf)
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

		customerToUpdate.setFullName(customer.getFullName());
		customerToUpdate.setEmail(customer.getEmail());
		customerToUpdate.setZipCode(customer.getZipCode());
		customerToUpdate.setAddress(customer.getAddress());
		customerToUpdate.setCity(customer.getCity());
		customerToUpdate.setState(customer.getState());
		customerToUpdate.setCountry(customer.getCountry());

		return this.customerRepository.save(customerToUpdate);
	}

	public String deleteCustomer(String cpf) {
		Customer customerToDelete = this.customerRepository.findById(cpf)
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

		this.customerRepository.delete(customerToDelete);

		// Customer deleted successfully
		return String.format(CUSTOMER_DELETED, customerToDelete.getFullName(), customerToDelete.getCpf());
	}
}
