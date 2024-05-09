package br.com.fiap.mslogistic.controller;

import br.com.fiap.mslogistic.controller.exception.ControllerExceptionHandler;
import br.com.fiap.mslogistic.entity.Purchase;
import br.com.fiap.mslogistic.entity.PurchaseHistory;
import br.com.fiap.mslogistic.service.LogisticService;
import br.com.fiap.mslogistic.utils.PurchaseHistoryUtils;
import br.com.fiap.mslogistic.utils.PurchaseUtils;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LogisticControllerUnitTest {
	private MockMvc mockMvc;

	@Mock
	private LogisticService logisticService;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		LogisticController logisticController = new LogisticController(logisticService);
		mockMvc = MockMvcBuilders.standaloneSetup(logisticController)
				.setControllerAdvice(new ControllerExceptionHandler())
				.build();
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Nested
	class GetHistoryByPurchaseId{
		@Test
		void allowGetHistoryByPurchaseId() throws Exception {
			//Arrange
			Long id = 1L;
			Purchase purchase  = PurchaseUtils.createFakePurchase(id);
			PurchaseHistory ph1 = PurchaseHistoryUtils.createFakePurchaseHistory(purchase.getId(), "AGUARDANDO PAGAMENTO");
			PurchaseHistory ph2 = PurchaseHistoryUtils.createFakePurchaseHistory(purchase.getId(), "PAGO");
			List<PurchaseHistory> purchaseHistory = List.of(ph1, ph2);
			when(logisticService.getHistoryByPurchaseId(any(Long.class))).thenReturn(purchaseHistory);

			//Act && Assert
			mockMvc.perform(get("/logistic//getHistoryByPurchaseId/{purchaseId}", purchase.getId())
					).andExpect(status().isOk());
			verify(logisticService, times(1)).getHistoryByPurchaseId(any(Long.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenGetHistoryByPurchaseId() throws Exception {
			//Arrange
			Long purchaseId = 99999999L;
			when(logisticService.getHistoryByPurchaseId(any(Long.class))).thenThrow(EntityNotFoundException.class);

			//Act && Assert
			mockMvc.perform(get("/logistic//getHistoryByPurchaseId/{purchaseId}", purchaseId)
					).andExpect(status().isNotFound());
			verify(logisticService, times(1)).getHistoryByPurchaseId(any(Long.class));
		}
	}

	@Nested
	class DispatchPurchases{
		@Test
		void allowDispatchPurchases() throws Exception{
			//Arrange
			String status = "AGUARDANDO ENTREGA";
			Purchase p1 = PurchaseUtils.createFakePurchase(1L, status);
			Purchase p2 = PurchaseUtils.createFakePurchase(2L, status);
			List<Purchase> purchaseList = List.of(p1, p2);
			when(logisticService.dispatchPurchases()).thenReturn(purchaseList);

			//Act && Assert
			mockMvc.perform(put("/logistic/dispatchPurchases")
					).andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.content().json(PurchaseUtils.asJsonString(purchaseList)));
			verify(logisticService, times(1)).dispatchPurchases();
		}
	}

	@Nested
	class DeliveryPurchase{
		@Test
		void allowDeliveryPurchase() throws Exception{
			//Arrange
			Long purchaseId = 1L;
			Purchase purchase = PurchaseUtils.createFakePurchase(purchaseId, "AGUARDANDO ENTREGA");
			when(logisticService.deliveryPurchase(any(Long.class)))
					.thenReturn(String.format(LogisticService.PURCHASE_DELIVERED, purchase.getId()));

			//Act && Assert
			mockMvc.perform(put("/logistic/deliveryPurchase/{purchaseId}", purchaseId)
					).andExpect(status().isOk())
					.andExpect(MockMvcResultMatchers.content().string(String.format(LogisticService.PURCHASE_DELIVERED, purchase.getId())));
			verify(logisticService, times(1)).deliveryPurchase(any(Long.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenDeliveryPurchase() throws Exception{
			//Arrange
			Long purchaseId = 99999999L;
			when(logisticService.deliveryPurchase(any(Long.class))).thenThrow(EntityNotFoundException.class);

			//Act && Assert
			mockMvc.perform(put("/logistic/deliveryPurchase/{purchaseId}", purchaseId)
					).andExpect(status().isNotFound());
			verify(logisticService, times(1)).deliveryPurchase(any(Long.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenDeliveryPurchase() throws Exception{
			//Arrange
			Long purchaseId = 1L;
			Purchase purchase = PurchaseUtils.createFakePurchase(purchaseId, "PAGO");
			when(logisticService.deliveryPurchase(any(Long.class))).thenThrow(DataIntegrityViolationException.class);

			//Act && Assert
			mockMvc.perform(put("/logistic/deliveryPurchase/{purchaseId}", purchase.getId())
					).andExpect(status().isBadRequest());
			verify(logisticService, times(1)).deliveryPurchase(any(Long.class));
		}
	}
}
