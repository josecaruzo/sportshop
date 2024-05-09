package br.com.fiap.mscustomers.repository;

import br.com.fiap.mscustomers.entity.Customer;
import br.com.fiap.mscustomers.utils.CustomerUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@Rollback //Rollback the transaction after each test
class CustomerRepositoryUnitTest {

	@Mock
	private CustomerRepository customerRepository;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Test
	void allowFindByCpf() {
		//Arrange
		String cpf = "822.685.230-71";
		Customer customer = CustomerUtils.createFakeCustomer();
		customer.setCpf(cpf);

		when(customerRepository.findById(any(String.class))).thenReturn(Optional.of(customer));

		//Act
		Optional<Customer> findCustomer = customerRepository.findById(cpf);

		//Assert
		assertThat(findCustomer).isPresent().isEqualTo(Optional.of(customer));
		verify(customerRepository, times(1)).findById(any(String.class));
	}

	@Test
	void allowFindByFullName() {
		//Arrange
		String name = " da Silva Sauro";
		Customer customer1 = CustomerUtils.createFakeCustomer("822.685.230-71","Fulano" + name);
		Customer customer2 = CustomerUtils.createFakeCustomer("905.013.870-56", "Ciclano" + name);

		List<Customer> customerList = List.of(customer1, customer2);
		when(customerRepository.findByFullNameContainingIgnoreCase(any(String.class))).thenReturn(customerList);

		//Act
		List<Customer> customers = customerRepository.findByFullNameContainingIgnoreCase(name);

		//Assert
		assertThat(customers).isNotEmpty().isEqualTo(customerList);
		verify(customerRepository, times(1)).findByFullNameContainingIgnoreCase(any(String.class));
	}

	@Test
	void allowCreateCustomer() {
		//Arrange
		Customer customer = CustomerUtils.createFakeCustomer();
		when(customerRepository.save(any(Customer.class))).thenReturn(customer);

		//Act
		Customer savedCustomer = customerRepository.save(customer);

		//Assert
		assertThat(savedCustomer).isNotNull().isEqualTo(customer);
		verify(customerRepository, times(1)).save(any(Customer.class));
	}

	@Test
	void allowUpdateCustomer() {
		//Arrange
		Customer customer = CustomerUtils.createFakeCustomer();
		customer.setEmail("newemail@email.com");

		when(customerRepository.save(any(Customer.class))).thenReturn(customer);

		//Act
		Customer savedCustomer = customerRepository.save(customer);

		//Assert
		assertThat(savedCustomer).isNotNull().isEqualTo(customer);
		verify(customerRepository, times(1)).save(any(Customer.class));
	}

	@Test
	void allowDeleteCustomer() {
		//Arrange
		Customer customer = CustomerUtils.createFakeCustomer();
		doNothing().when(customerRepository).delete(any(Customer.class));

		//Act
		customerRepository.delete(customer);
		Optional<Customer> findCustomer = customerRepository.findById(customer.getCpf());

		//Assert
		assertThat(findCustomer).isNotPresent();
		verify(customerRepository, times(1)).delete(any(Customer.class));
		verify(customerRepository, times(1)).findById(any(String.class));
	}
}
