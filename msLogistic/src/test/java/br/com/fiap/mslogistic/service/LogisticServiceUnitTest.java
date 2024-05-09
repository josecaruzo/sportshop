package br.com.fiap.mslogistic.service;

import br.com.fiap.mslogistic.entity.Purchase;
import br.com.fiap.mslogistic.entity.PurchaseHistory;
import br.com.fiap.mslogistic.repository.PurchaseHistoryRepository;
import br.com.fiap.mslogistic.repository.PurchaseRepository;
import br.com.fiap.mslogistic.utils.PurchaseHistoryUtils;
import br.com.fiap.mslogistic.utils.PurchaseUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LogisticServiceUnitTest {

	private LogisticService logisticService;

	@Mock
	private PurchaseRepository purchaseRepository;

	@Mock
	private PurchaseHistoryRepository purchaseHistoryRepository;

	AutoCloseable openMocks;

	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		logisticService = new LogisticService(purchaseRepository, purchaseHistoryRepository);
	}

	@AfterEach
	void tearDown() throws Exception {
		openMocks.close();
	}

	@Nested
	class GetHistoryByPurchaseId{
		@Test
		void allowGetHistoryByPurchaseId() {
			//Arrange
			Long purchaseId = 1L;
			PurchaseHistory ph1 = PurchaseHistoryUtils.createFakePurchaseHistory(purchaseId, "AGUARDANDO PAGAMENTO");
			PurchaseHistory ph2 = PurchaseHistoryUtils.createFakePurchaseHistory(purchaseId, "PAGO");
			List<PurchaseHistory> purchaseHistory = List.of(ph1, ph2);
			when(purchaseHistoryRepository.findByPurchaseId(any(Long.class))).thenReturn(purchaseHistory);

			//Act
			List<PurchaseHistory> findHistory = logisticService.getHistoryByPurchaseId(purchaseId);

			//Assert
			assertThat(findHistory).isNotNull().isEqualTo(purchaseHistory);
			verify(purchaseHistoryRepository, times(1)).findByPurchaseId(any(Long.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenGetHistoryByPurchaseId() {
			//Arrange
			Long purchaseId = 1L;
			when(purchaseHistoryRepository.findByPurchaseId(any(Long.class))).thenReturn(List.of());

			//Act && Assert
			assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> logisticService.getHistoryByPurchaseId(purchaseId));
			verify(purchaseHistoryRepository, times(1)).findByPurchaseId(any(Long.class));
		}
	}

	@Nested
	class CreatePurchaseHistory {
		@Test
		void allowCreatePurchaseHistory(){
			//Arrange
			PurchaseHistory ph1 = PurchaseHistoryUtils.createFakePurchaseHistory(1L, "AGUARDANDO PAGAMENTO");
			when(purchaseHistoryRepository.save(any(PurchaseHistory.class))).thenAnswer(p -> p.getArgument(0));

			//Act
			PurchaseHistory savedHistory = logisticService.createPurchaseHistory(ph1);

			//Assert
			assertThat(savedHistory).isNotNull().isEqualTo(ph1);
			verify(purchaseHistoryRepository, times(1)).save(any(PurchaseHistory.class));
		}
	}

	@Nested
	class DispatchPurchases{
		@Test
		void allowDispatchPurchases(){
			//Arrange
			String status = "PAGO"; //Initial status
			Purchase p1 = PurchaseUtils.createFakePurchase(1L, status);
			p1.setDeliveryZipCode("12390-000"); //Different zip code to create a new delivery group
			Purchase p2 = PurchaseUtils.createFakePurchase(2L, status);
			p1.setDeliveryZipCode("12380-000"); //Different zip code to create a new delivery group
			List<Purchase> purchaseList = List.of(p1, p2);
			when(purchaseRepository.findByStatusOrderByDeliveryZipCode(any(String.class))).thenReturn(purchaseList);
			when(purchaseRepository.save(any(Purchase.class))).thenAnswer(p -> p.getArgument(0));
			when(purchaseHistoryRepository.save(any(PurchaseHistory.class))).thenAnswer(p -> p.getArgument(0));

			//Act
			List<Purchase> purchases = logisticService.dispatchPurchases();

			//Assert
			assertThat(purchases).hasSize(2);
			assertThat(purchases.get(0).getDeliveryGroup()).isNotNull();
			assertThat(purchases.get(0).getStatus()).isEqualTo("AGUARDANDO ENTREGA");

			verify(purchaseRepository, times(1)).findByStatusOrderByDeliveryZipCode(any(String.class));
			verify(purchaseRepository, times(2)).save(any(Purchase.class));
			verify(purchaseHistoryRepository, times(2)).save(any(PurchaseHistory.class));
		}
	}

	@Nested
	class DeliveryPurchase{
		@Test
		void allowDeliveryPurchase(){
			//Arrange
			Long purchaseId = 1L;
			Purchase purchase = PurchaseUtils.createFakePurchase(purchaseId, "AGUARDANDO ENTREGA");
			when(purchaseRepository.findById(any(Long.class))).thenReturn(Optional.of(purchase));
			when(purchaseRepository.save(any(Purchase.class))).thenAnswer(p -> p.getArgument(0));
			when(purchaseHistoryRepository.save(any(PurchaseHistory.class))).thenAnswer(p -> p.getArgument(0));

			//Act
			String deliveredPurchase = logisticService.deliveryPurchase(purchaseId);

			//Assert
			assertThat(deliveredPurchase).isNotNull().isEqualTo(String.format(LogisticService.PURCHASE_DELIVERED, purchaseId));
			verify(purchaseRepository, times(1)).findById(any(Long.class));
			verify(purchaseRepository, times(1)).save(any(Purchase.class));
			verify(purchaseHistoryRepository, times(1)).save(any(PurchaseHistory.class));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenDeliveryPurchase(){
			//Arrange
			Long purchaseId = 1L;
			when(purchaseRepository.findById(any(Long.class))).thenReturn(Optional.empty());

			//Act && Assert
			assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> logisticService.deliveryPurchase(purchaseId));
			verify(purchaseRepository, times(1)).findById(any(Long.class));
			verify(purchaseRepository, never()).save(any(Purchase.class));
			verify(purchaseHistoryRepository, never()).save(any(PurchaseHistory.class));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenDeliveryPurchase(){
			//Arrange
			Long purchaseId = 1L;
			Purchase purchase = PurchaseUtils.createFakePurchase(purchaseId, "PAGO");
			when(purchaseRepository.findById(any(Long.class))).thenReturn(Optional.of(purchase));

			//Act && Assert
			assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> logisticService.deliveryPurchase(purchaseId));
			verify(purchaseRepository, times(1)).findById(any(Long.class));
			verify(purchaseRepository, never()).save(any(Purchase.class));
			verify(purchaseHistoryRepository, never()).save(any(PurchaseHistory.class));
		}
	}
}
