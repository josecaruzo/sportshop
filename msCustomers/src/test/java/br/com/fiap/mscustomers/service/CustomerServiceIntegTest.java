package br.com.fiap.mscustomers.service;

import br.com.fiap.mscustomers.entity.Customer;
import br.com.fiap.mscustomers.utils.CustomerUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class CustomerServiceIntegTest {
	@Autowired
	private CustomerService customerService;

	@Nested
	class GetByCpf {
		@Test
		void allowGetByCpf() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();

			//Act
			Customer savedCustomer = customerService.createCustomer(customer);
			Customer findCustomer = customerService.getByCpf(customer.getCpf());

			//Assert
			assertThat(findCustomer).isNotNull().isEqualTo(savedCustomer);
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenGetByCpf() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();

			// Act && Assert
			assertThatThrownBy(() -> customerService.getByCpf(customer.getCpf()))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(CustomerService.ENTITY_NOT_FOUND);
		}
	}

	@Nested
	class GetByName {
		@Test
		void allowGetByName() {
			//Arrange
			String name = " da Silva Sauro";
			Customer customer1 = CustomerUtils.createFakeCustomer("822.685.230-71","Fulano" + name);
			Customer customer2 = CustomerUtils.createFakeCustomer("905.013.870-56", "Ciclano" + name);

			//Act
			Customer createdCustomer1 = customerService.createCustomer(customer1);
			Customer createdCustomer2 = customerService.createCustomer(customer2);
			List<Customer> customerList = List.of(createdCustomer1, createdCustomer2);

			List<Customer> customers = customerService.getByName(name);

			//Assert
			assertThat(customers).isNotEmpty().isEqualTo(customerList);
		}
	}

	@Nested
	class CreateCustomer {
		@Test
		void allowCreateCustomer() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();

			//Act
			Customer createdCustomer = customerService.createCustomer(customer);

			//Assert
			assertThat(createdCustomer).isNotNull().isEqualTo(customer);
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenCreateCustomer() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();

			// Act
			customerService.createCustomer(customer);

			//Assert
			assertThatThrownBy(() -> customerService.createCustomer(customer))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(CustomerService.CPF_ALREADY_EXISTS); //Trying to create same customer twice

		}
	}

	@Nested
	class UpdateCustomer {
		@Test
		void allowUpdateCustomer() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer(); //This customer will be updated on UpdateCustomer method
			Customer oldCustomer = CustomerUtils.createFakeCustomer(); //Customer to validate old value
			Customer newCustomer = CustomerUtils.createFakeCustomer(); //Customer to validate new value
			newCustomer.setEmail("newemail@email.com");

			//Act
			customer = customerService.createCustomer(customer);
			Customer updatedCustomer = customerService.updateCustomer(customer.getCpf(), newCustomer);

			//Assert
			assertThat(updatedCustomer).isNotNull().isNotEqualTo(oldCustomer).isEqualTo(newCustomer);
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenUpdateCustomer() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			Customer anotherCustomer = CustomerUtils.createFakeCustomer();

			// Act && Assert
			assertThatThrownBy(() -> customerService.updateCustomer(customer.getCpf(), anotherCustomer))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(CustomerService.ENTITY_NOT_FOUND); //Updating a non-existent customer
		}
	}

	@Nested
	class DeleteCustomer {
		@Test
		void allowDeleteCustomer() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();

			//Act
			customer = customerService.createCustomer(customer);
			String message = customerService.deleteCustomer(customer.getCpf());

			//Assert
			assertThat(message).isNotNull().isEqualTo(String.format(CustomerService.CUSTOMER_DELETED, customer.getFullName(), customer.getCpf()));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenDeleteCustomer() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();

			// Act && Assert
			assertThatThrownBy(() -> customerService.deleteCustomer(customer.getCpf()))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(CustomerService.ENTITY_NOT_FOUND); //Deleting a non-existent customer
		}
	}
}
