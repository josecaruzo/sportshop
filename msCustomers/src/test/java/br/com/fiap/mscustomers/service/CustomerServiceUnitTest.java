package br.com.fiap.mscustomers.service;

import br.com.fiap.mscustomers.entity.Customer;
import br.com.fiap.mscustomers.repository.CustomerRepository;
import br.com.fiap.mscustomers.utils.CustomerUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceUnitTest {

	private CustomerService customerService;

	@Mock
	private CustomerRepository customerRepository;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		customerService = new CustomerService(customerRepository);
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Nested
	class GetByCpf {
		@Test
		void allowGetByCpf() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			when(customerRepository.findById(any(String.class))).thenReturn(Optional.of(customer));

			//Act
			Customer findCustomer = customerService.getByCpf(customer.getCpf());

			//Assert
			assertThat(findCustomer).isNotNull().isEqualTo(customer);
			verify(customerRepository, times(1)).findById(any(String.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenGetByCpf() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			when(customerRepository.findById(any(String.class))).thenReturn(Optional.empty());

			// Act && Assert
			assertThatThrownBy(() -> customerService.getByCpf(customer.getCpf()))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(CustomerService.ENTITY_NOT_FOUND);
			verify(customerRepository, times(1)).findById(any(String.class));
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

			List<Customer> customerList = List.of(customer1, customer2);
			when(customerRepository.findByFullNameContainingIgnoreCase(any(String.class))).thenReturn(customerList);

			//Act
			List<Customer> customers = customerService.getByName(name);

			//Assert
			assertThat(customers).isNotEmpty().isEqualTo(customerList);
			verify(customerRepository, times(1)).findByFullNameContainingIgnoreCase(any(String.class));
		}
	}

	@Nested
	class CreateCustomer {
		@Test
		void allowCreateCustomer() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			when(customerRepository.save(any(Customer.class))).thenAnswer(c -> c.getArgument(0));

			//Act
			Customer savedCustomer = customerService.createCustomer(customer);

			//Assert
			assertThat(savedCustomer).isNotNull().isEqualTo(customer);
			verify(customerRepository, times(1)).save(any(Customer.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenCreateCustomer() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();

			when(customerRepository.findById(any(String.class))).thenReturn(Optional.of(customer));

			// Act && Assert
			assertThatThrownBy(() -> customerService.createCustomer(customer))
					.isInstanceOf(DataIntegrityViolationException.class)
					.hasMessage(CustomerService.CPF_ALREADY_EXISTS); //Trying to create same customer twice
			verify(customerRepository, times(1)).findById(any(String.class));
			verify(customerRepository, never()).save(any(Customer.class));
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

			when(customerRepository.findById(any(String.class))).thenReturn(Optional.of(customer));
			when(customerRepository.save(any(Customer.class))).thenAnswer(c -> c.getArgument(0));

			//Act
			Customer updatedCustomer = customerService.updateCustomer(oldCustomer.getCpf(), newCustomer);

			//Assert
			assertThat(updatedCustomer).isNotNull().isNotEqualTo(oldCustomer).isEqualTo(newCustomer);
			verify(customerRepository, times(1)).findById(any(String.class));
			verify(customerRepository, times(1)).save(any(Customer.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenUpdateCustomer() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			Customer anotherCustomer = CustomerUtils.createFakeCustomer();
			when(customerRepository.findById(any(String.class))).thenReturn(Optional.empty());

			// Act && Assert
			assertThatThrownBy(() -> customerService.updateCustomer(customer.getCpf(), anotherCustomer))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(CustomerService.ENTITY_NOT_FOUND);
			verify(customerRepository, times(1)).findById(any(String.class));
			verify(customerRepository, never()).save(any(Customer.class));
		}
	}

	@Nested
	class DeleteCustomer {
		@Test
		void allowDeleteCustomer() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			when(customerRepository.findById(any(String.class))).thenReturn(Optional.of(customer));
			doNothing().when(customerRepository).delete(any(Customer.class));

			//Act
			String message = customerService.deleteCustomer(customer.getCpf());

			//Assert
			assertThat(message).isNotNull().isEqualTo(String.format(CustomerService.CUSTOMER_DELETED, customer.getFullName(), customer.getCpf()));
			verify(customerRepository, times(1)).findById(any(String.class));
			verify(customerRepository, times(1)).delete(any(Customer.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenDeleteCustomer() {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			when(customerRepository.findById(any(String.class))).thenReturn(Optional.empty());

			// Act && Assert
			assertThatThrownBy(() -> customerService.deleteCustomer(customer.getCpf()))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage(CustomerService.ENTITY_NOT_FOUND);
			verify(customerRepository, times(1)).findById(any(String.class));
			verify(customerRepository, never()).delete(any(Customer.class));
		}
	}
}
