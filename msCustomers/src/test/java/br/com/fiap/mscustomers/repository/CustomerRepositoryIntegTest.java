package br.com.fiap.mscustomers.repository;

import br.com.fiap.mscustomers.entity.Customer;
import br.com.fiap.mscustomers.utils.CustomerUtils;
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
class CustomerRepositoryIntegTest {

	@Autowired
	private CustomerRepository customerRepository;

	@Test
	void allowFindByCpf() {
		//Arrange
		String cpf = "822.685.230-71";
		Customer customer = CustomerUtils.createFakeCustomer();
		customer.setCpf(cpf);

		//Act
		Customer createdCustomer = customerRepository.save(customer);
		Optional<Customer> findCustomer = customerRepository.findById(createdCustomer.getCpf());

		//Assert
		assertThat(findCustomer).isPresent().isEqualTo(Optional.of(customer));
	}

	@Test
	void allowFindByFullName() {
		//Arrange
		String name = " da Silva Sauro";
		Customer customer1 = CustomerUtils.createFakeCustomer("822.685.230-71","Fulano" + name);
		Customer customer2 = CustomerUtils.createFakeCustomer("905.013.870-56", "Ciclano" + name);

		//Act
		Customer createdCustomer1 = customerRepository.save(customer1);
		Customer createdCustomer2 = customerRepository.save(customer2);
		List<Customer> customerList = List.of(createdCustomer1, createdCustomer2);

		List<Customer> customers = customerRepository.findByFullNameContainingIgnoreCase(name);

		//Assert
		assertThat(customers).isNotEmpty().isEqualTo(customerList);
	}

	@Test
	void allowCreateCustomer() {
		//Arrange
		Customer customer = CustomerUtils.createFakeCustomer();

		//Act
		Customer createdCustomer = customerRepository.save(customer);

		//Assert
		assertThat(createdCustomer).isInstanceOf(Customer.class).isNotNull().isEqualTo(customer);
	}

	@Test
	void allowUpdateCustomer() {
		//Arrange
		Customer customer = CustomerUtils.createFakeCustomer();
		customer.setEmail("newemail@email.com");

		//Act
		Customer createdCustomer = customerRepository.save(customer);

		//Assert
		assertThat(createdCustomer).isInstanceOf(Customer.class).isNotNull().isEqualTo(customer);
	}

	@Test
	void allowDeleteCustomer() {
		//Arrange
		Customer customer = CustomerUtils.createFakeCustomer();

		//Act
		Customer createdCustomer = customerRepository.save(customer);
		customerRepository.delete(createdCustomer);
		Customer findCustomer = customerRepository.findById(createdCustomer.getCpf()).orElse(null);

		//Assert
		assertThat(findCustomer).isNull();
	}
}
