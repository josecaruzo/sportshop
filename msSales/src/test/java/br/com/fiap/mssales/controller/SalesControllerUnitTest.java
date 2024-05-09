package br.com.fiap.mssales.controller;

import br.com.fiap.mssales.controller.exception.ControllerExceptionHandler;
import br.com.fiap.mssales.entity.Purchase;
import br.com.fiap.mssales.service.SalesService;
import br.com.fiap.mssales.utils.PurchaseUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SalesControllerUnitTest {
	private MockMvc mockMvc;

	@Mock
	private SalesService salesService;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		SalesController salesController = new SalesController(salesService);
		mockMvc = MockMvcBuilders.standaloneSetup(salesController)
				.setControllerAdvice(new ControllerExceptionHandler())
				.build();
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}


	@Nested
	class GetPurchaseByID {
		@Test
		void allowGetPurchaseById() throws Exception {
			//Arrange
			Long id = 1L;
			Purchase purchase = PurchaseUtils.createFakePurchase(id);
			when(salesService.getPurchaseById(any(Long.class))).thenReturn(purchase);

			//Act
			mockMvc.perform(get("/sales/getPurchaseById/" + id))
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));

			//Assert
			verify(salesService, times(1)).getPurchaseById(any(Long.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenGetPurchaseById() throws Exception {
			//Arrange
			Long id = 9999999L;
			when(salesService.getPurchaseById(any(Long.class))).thenThrow(EntityNotFoundException.class);

			//Act
			mockMvc.perform(get("/sales/getPurchaseById/" + id))
					.andExpect(status().isNotFound());

			//Assert
			verify(salesService, times(1)).getPurchaseById(any(Long.class));
		}
	}

	@Nested
	class GetPurchaseByStatus {
		@Test
		void allowGetPurchasesByStatus() throws Exception {
			//Arrange
			String status = "PAGO";
			Purchase p1 = PurchaseUtils.createFakePurchase(status);
			Purchase p2 = PurchaseUtils.createFakePurchase(status);
			when(salesService.getPurchasesByStatus(any(String.class))).thenReturn(List.of(p1, p2));

			//Act
			mockMvc.perform(get("/sales/getPurchasesByStatus/" + status))
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.content().json(PurchaseUtils.asJsonString(List.of(p1, p2))));

			//Assert
			verify(salesService, times(1)).getPurchasesByStatus(any(String.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenGetPurchasesByStatus() throws Exception {
			//Arrange
			String status = "STATUS TEST";
			when(salesService.getPurchasesByStatus(any(String.class))).thenThrow(EntityNotFoundException.class);

			//Act
			mockMvc.perform(get("/sales/getPurchasesByStatus/" + status))
					.andExpect(status().isNotFound());

			//Assert
			verify(salesService, times(1)).getPurchasesByStatus(any(String.class));
		}
	}

	@Nested
	class CreatePurchase {
		@Test
		void allowCreatePurchase() throws Exception {
			//Arrange
			Purchase purchase = PurchaseUtils.createFakePurchase(1L);
			when(salesService.createPurchase(any(Purchase.class))).thenReturn(purchase);

			//Act
			mockMvc.perform(post("/sales/createPurchase")
					.contentType("application/json")
					.content(PurchaseUtils.asJsonString(purchase)))
					.andExpect(status().isCreated());

			//Assert
			verify(salesService, times(1)).createPurchase(any(Purchase.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenCreatePurchase_Customer() throws Exception {
			//Arrange
			Purchase purchase = PurchaseUtils.createFakePurchase(1L);
			purchase.setCustomerCpf("553.628.452-50"); // Not found customer
			when(salesService.createPurchase(any(Purchase.class))).thenThrow(EntityNotFoundException.class);

			//Act
			mockMvc.perform(post("/sales/createPurchase")
					.contentType("application/json")
					.content(PurchaseUtils.asJsonString(purchase)))
					.andExpect(status().isNotFound());
			//Assert
			verify(salesService, times(1)).createPurchase(any(Purchase.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenCreatePurchase_Product() throws Exception {
			//Arrange
			Purchase purchase = PurchaseUtils.createFakePurchase(1L);
			purchase.getItems().get(0).setProductId(9999999L); // Not found product
			purchase.getItems().get(1).setProductId(9999999L); // Not found product
			when(salesService.createPurchase(any(Purchase.class))).thenThrow(EntityNotFoundException.class);

			//Act
			mockMvc.perform(post("/sales/createPurchase")
							.contentType("application/json")
							.content(PurchaseUtils.asJsonString(purchase)))
					.andExpect(status().isNotFound());

			//Assert
			verify(salesService, times(1)).createPurchase(any(Purchase.class));
		}

		@Test
		void shouldThrowMethodArgumentNotValidException_WhenCreatePurchase() throws Exception {
			//Arrange
			Purchase purchase = PurchaseUtils.createFakePurchase(1L);
			purchase.setCustomerCpf("00000000000"); // Invalid CPF
			purchase.getItems().get(0).setQuantity(-1); // Invalid quantity

			//Act
			mockMvc.perform(post("/sales/createPurchase")
							.contentType("application/json")
							.content(PurchaseUtils.asJsonString(purchase)))
					.andExpect(status().isBadRequest());

			//Assert
			verify(salesService, never()).createPurchase(any(Purchase.class));
		}
	}

	@Nested
	class PayPurchase {
		@Test
		void allowPayPurchase() throws Exception {
			//Arrange
			Long id = 1L;
			Purchase purchase = PurchaseUtils.createFakePurchase(id);
			when(salesService.payPurchase(any(Long.class))).thenReturn(purchase);

			//Act
			mockMvc.perform(put("/sales/payPurchase/id/" + id))
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));

			//Assert
			verify(salesService, times(1)).payPurchase(any(Long.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenPayPurchase() throws Exception {
			//Arrange
			Long id = 9999999L;
			when(salesService.payPurchase(any(Long.class))).thenThrow(EntityNotFoundException.class);

			//Act
			mockMvc.perform(put("/sales/payPurchase/id/" + id))
					.andExpect(status().isNotFound());

			//Assert
			verify(salesService, times(1)).payPurchase(any(Long.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenPayPurchase() throws Exception {
			//Arrange
			Long id = 1L;
			Purchase purchase = PurchaseUtils.createFakePurchase(id);
			purchase.setStatus("CANCELADO");

			when(salesService.payPurchase(any(Long.class))).thenThrow(DataIntegrityViolationException.class);

			//Act
			mockMvc.perform(put("/sales/payPurchase/id/" + id))
					.andExpect(status().isBadRequest());

			//Assert
			verify(salesService, times(1)).payPurchase(any(Long.class));
		}
	}

	@Nested
	class CancelPurchase {
		@Test
		void allowCancelPurchase() throws Exception {
			//Arrange
			Long id = 1L;
			Purchase purchase = PurchaseUtils.createFakePurchase(id);
			when(salesService.cancelPurchase(any(Long.class))).thenReturn(purchase);

			//Act
			mockMvc.perform(put("/sales/cancelPurchase/id/" + id))
					.andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));

			//Assert
			verify(salesService, times(1)).cancelPurchase(any(Long.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenCancelPurchase() throws Exception {
			//Arrange
			Long id = 9999999L;
			when(salesService.cancelPurchase(any(Long.class))).thenThrow(EntityNotFoundException.class);

			//Act
			mockMvc.perform(put("/sales/cancelPurchase/id/" + id))
					.andExpect(status().isNotFound());

			//Assert
			verify(salesService, times(1)).cancelPurchase(any(Long.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenCancelPurchase() throws Exception {
			//Arrange
			Long id = 1L;
			Purchase purchase = PurchaseUtils.createFakePurchase(id);
			purchase.setStatus("PAGO");

			when(salesService.cancelPurchase(any(Long.class))).thenThrow(DataIntegrityViolationException.class);

			//Act
			mockMvc.perform(put("/sales/cancelPurchase/id/" + id))
					.andExpect(status().isBadRequest());

			//Assert
			verify(salesService, times(1)).cancelPurchase(any(Long.class));
		}
	}
}
