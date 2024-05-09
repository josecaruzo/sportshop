package br.com.fiap.mslogistic.repository;

import br.com.fiap.mslogistic.entity.PurchaseHistory;
import br.com.fiap.mslogistic.utils.PurchaseHistoryUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PurchaseHistoryRepositoryUnitTest {
	@Mock
	private PurchaseHistoryRepository purchaseHistoryRepository;

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
	void allowFindByPurchaseId(){
		//Arrange
		PurchaseHistory ph1 = PurchaseHistoryUtils.createFakePurchaseHistory(1L, 1L, "AGUARDANDO PAGAMENTO");
		PurchaseHistory ph2 = PurchaseHistoryUtils.createFakePurchaseHistory(2L, 1L, "PAGO");
		List<PurchaseHistory> purchaseHistory = List.of(ph1, ph2);
		when(purchaseHistoryRepository.findByPurchaseId(any(Long.class))).thenReturn(purchaseHistory);

		//Act
		List<PurchaseHistory> findHistory = purchaseHistoryRepository.findByPurchaseId(1L);

		//Assert
		assertThat(findHistory).isNotNull().isEqualTo(purchaseHistory);
		verify(purchaseHistoryRepository, times(1)).findByPurchaseId(any(Long.class));
	}

	@Test
	void allowCreatePurchaseHistory(){
		//Arrange
		PurchaseHistory ph1 = PurchaseHistoryUtils.createFakePurchaseHistory(1L, 1L, "AGUARDANDO PAGAMENTO");
		when(purchaseHistoryRepository.save(any(PurchaseHistory.class))).thenReturn(ph1);

		//Act
		PurchaseHistory newHistory = purchaseHistoryRepository.save(ph1);

		//Assert
		assertThat(newHistory).isNotNull().isEqualTo(ph1);
	}
}
