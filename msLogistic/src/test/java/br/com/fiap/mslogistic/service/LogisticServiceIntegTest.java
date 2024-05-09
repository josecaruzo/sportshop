package br.com.fiap.mslogistic.service;

import br.com.fiap.mslogistic.entity.Purchase;
import br.com.fiap.mslogistic.entity.PurchaseHistory;
import br.com.fiap.mslogistic.repository.PurchaseHistoryRepository;
import br.com.fiap.mslogistic.repository.PurchaseRepository;
import br.com.fiap.mslogistic.utils.PurchaseHistoryUtils;
import br.com.fiap.mslogistic.utils.PurchaseUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@SpringBootTest
@Transactional
@Sql(scripts = {"/addData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Rollback
public class LogisticServiceIntegTest {
	@Autowired
	private LogisticService logisticService;

	@Nested
	class GetHistoryByPurchaseId{
		@Test
		void allowGetHistoryByPurchaseId() {
			//Arrange
			Long id = 1000000L;

			//Act
			List<PurchaseHistory> findHistory = logisticService.getHistoryByPurchaseId(id);

			//Assert
			assertThat(findHistory).hasSize(1);
			assertThat(findHistory.get(0).getId()).isEqualTo(id);
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenGetHistoryByPurchaseId() {
			//Arrange
			Long purchaseId = 99999999L;

			//Act && Assert
			assertThatExceptionOfType(EntityNotFoundException.class)
					.isThrownBy(() -> logisticService.getHistoryByPurchaseId(purchaseId));
		}
	}

	@Nested
	class CreatePurchaseHistory {
		@Test
		void allowCreatePurchaseHistory(){
			//Arrange
			Purchase purchase = PurchaseUtils.createFakePurchase(1000003L);
			String status = "STATUS TEST";
			PurchaseHistory history = PurchaseHistoryUtils.createFakePurchaseHistory( purchase.getId(), status);

			PurchaseHistory savedHistory = logisticService.createPurchaseHistory(history);

			//Assert
			assertThat(savedHistory).isNotNull();
			assertThat(savedHistory.getId()).isNotNull();
			assertThat(savedHistory.getPurchaseId()).isEqualTo(purchase.getId());
			assertThat(savedHistory.getStatus()).isEqualTo(status);
		}
	}

	@Nested
	class DispatchPurchases{
		@Test
		void allowDispatchPurchases(){
			//Act
			List<Purchase> purchases = logisticService.dispatchPurchases();

			//Assert
			assertThat(purchases).hasSizeGreaterThan(0);
			assertThat(purchases.get(0).getDeliveryGroup()).isNotNull();
			assertThat(purchases.get(0).getStatus()).isEqualTo("AGUARDANDO ENTREGA");
		}
	}

	@Nested
	class DeliveryPurchase{
		@Test
		void allowDeliveryPurchase(){
			//Arrange
			Long id = 1000003L;

			//Act
			String deliveredPurchase = logisticService.deliveryPurchase(id);

			//Assert
			assertThat(deliveredPurchase).isNotNull().isEqualTo(String.format(LogisticService.PURCHASE_DELIVERED, id));
		}

		@Test
		void shouldThrowEntityNotFoundException_WhenDeliveryPurchase(){
			//Arrange
			Long purchaseId = 99999999L;

			//Act && Assert
			assertThatExceptionOfType(EntityNotFoundException.class)
					.isThrownBy(() -> logisticService.deliveryPurchase(purchaseId));
		}

		@Test
		void shouldThrowDataIntegrityViolationException_WhenDeliveryPurchase(){
			//Arrange
			Long id = 1000000L;

			//Act && Assert
			assertThatExceptionOfType(DataIntegrityViolationException.class)
					.isThrownBy(() -> logisticService.deliveryPurchase(id));
		}
	}
}
