package br.com.fiap.mscustomers.controller;

import br.com.fiap.mscustomers.controller.exception.ControllerExceptionHandler;
import br.com.fiap.mscustomers.entity.Customer;
import br.com.fiap.mscustomers.service.CustomerService;
import br.com.fiap.mscustomers.utils.CustomerUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerControllerUnitTest {

	private MockMvc mockMvc;

	@Mock
	private CustomerService customerService;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		CustomerController customerController = new CustomerController(customerService);
		mockMvc = MockMvcBuilders.standaloneSetup(customerController)
				.setControllerAdvice(new ControllerExceptionHandler())
				.build();
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Nested
	class GetCustomerByCpf{
		@Test
		void shouldGetCustomerByCpf() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			String cpf = customer.getCpf();
			when(customerService.getByCpf(any(String.class))).thenReturn(customer);

			//Act && Assert
			mockMvc.perform(get("/customers/getCustomerByCpf/{cpf}", cpf)
			).andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.content().json(CustomerUtils.asJsonString(customer)));
			verify(customerService, times(1)).getByCpf(any(String.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenGetCustomerByCpf() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			String cpf = customer.getCpf();
			when(customerService.getByCpf(any(String.class))).thenThrow(EntityNotFoundException.class);

			//Act && Assert
			mockMvc.perform(get("/customers/getCustomerByCpf/{cpf}", cpf)
			).andExpect(status().isNotFound());
			verify(customerService, times(1)).getByCpf(any(String.class));
		}
	}

	@Nested
	class GetCustomerByName{
		@Test
		void shouldGetCustomerByName() throws Exception {
			//Arrange
			String name = " da Silva Sauro";
			Customer customer1 = CustomerUtils.createFakeCustomer("822.685.230-71","Fulano" + name);
			Customer customer2 = CustomerUtils.createFakeCustomer("905.013.870-56", "Ciclano" + name);
			List<Customer> customerList = List.of(customer1, customer2);

			when(customerService.getByName(any(String.class))).thenReturn(customerList);

			//Act && Assert
			mockMvc.perform(get("/customers/getCustomersByName/{name}", name)
			).andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.content().json(CustomerUtils.asJsonString(customerList)));
			verify(customerService, times(1)).getByName(any(String.class));
		}
	}

	@Nested
	class CreateCustomer{
		@Test
		void shouldCreateCustomer() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			when(customerService.createCustomer(any(Customer.class))).thenAnswer(c -> c.getArgument(0));

			//Act && Assert
			mockMvc.perform(post("/customers/createCustomer")
							.contentType(MediaType.APPLICATION_JSON)
							.content(CustomerUtils.asJsonString(customer))
					).andExpect(status().isCreated())
					.andExpect(MockMvcResultMatchers.content().json(CustomerUtils.asJsonString(customer)));
			verify(customerService, times(1)).createCustomer(any(Customer.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenCreateCustomer() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			when(customerService.createCustomer(any(Customer.class))).thenThrow(DataIntegrityViolationException.class);

			//Act && Assert
			mockMvc.perform(post("/customers/createCustomer")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customer))
			).andExpect(status().isBadRequest());
			verify(customerService, times(1)).createCustomer(any(Customer.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenCreateCustomer() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			customer.setEmail("email"); //wrong email type
			customer.setZipCode("000"); //wrong zip code

			//Act && Assert
			mockMvc.perform(post("/customers/createCustomer")
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(customer))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).createCustomer(any(Customer.class));
		}
	}

	@Nested
	class UpdateCustomer{
		@Test
		void shouldUpdateCustomer() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer(); //This customer will be updated on UpdateCustomer method
			Customer newCustomer = CustomerUtils.createFakeCustomer();
			newCustomer.setEmail("newemail@email.com");

			when(customerService.updateCustomer(any(String.class), any(Customer.class))).thenAnswer(c -> c.getArgument(1));

			//Act && Assert
			mockMvc.perform(put("/customers/updateCustomer/{cpf}", customer.getCpf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(newCustomer))
			).andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.content().json(CustomerUtils.asJsonString(newCustomer)));

			verify(customerService, times(1)).updateCustomer(any(String.class),any(Customer.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenUpdateCustomer() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer(); //This customer will be updated on UpdateCustomer method
			Customer newCustomer = CustomerUtils.createFakeCustomer();
			newCustomer.setEmail("newemail@email.com");

			when(customerService.updateCustomer(any(String.class), any(Customer.class))).thenThrow(EntityNotFoundException.class);

			//Act && Assert
			mockMvc.perform(put("/customers/updateCustomer/{cpf}", customer.getCpf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(newCustomer))
			).andExpect(status().isNotFound());

			verify(customerService, times(1)).updateCustomer(any(String.class),any(Customer.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenUpdateCustomer() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer(); //This customer will be updated on UpdateCustomer method
			Customer newCustomer = CustomerUtils.createFakeCustomer();
			newCustomer.setEmail("email"); //wrong email type
			newCustomer.setZipCode("000"); //wrong zip code

			//Act && Assert
			mockMvc.perform(put("/customers/updateCustomer/{cpf}", customer.getCpf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(CustomerUtils.asJsonString(newCustomer))
			).andExpect(status().isBadRequest());
			verify(customerService, never()).updateCustomer(any(String.class),any(Customer.class));
		}
	}

	@Nested
	class DeleteCustomer{
		@Test
		void shouldDeleteCustomer() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			String deleteMessage = String.format(CustomerService.CUSTOMER_DELETED, customer.getFullName(), customer.getCpf());

			when(customerService.deleteCustomer(any(String.class))).thenReturn(deleteMessage);

			//Act && Assert
			mockMvc.perform(delete("/customers/deleteCustomer/{cpf}", customer.getCpf())
			).andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.content().string(deleteMessage));
			verify(customerService, times(1)).deleteCustomer(any(String.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenDeleteCustomer() throws Exception {
			//Arrange
			Customer customer = CustomerUtils.createFakeCustomer();
			when(customerService.deleteCustomer(any(String.class))).thenThrow(EntityNotFoundException.class);

			//Act && Assert
			mockMvc.perform(delete("/customers/deleteCustomer/{cpf}", customer.getCpf())
					).andExpect(status().isNotFound());
			verify(customerService, times(1)).deleteCustomer(any(String.class));
		}
	}
}
