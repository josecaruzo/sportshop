package br.com.fiap.mslogistic.repository;


import br.com.fiap.mslogistic.entity.PurchaseHistory;
import br.com.fiap.mslogistic.utils.PurchaseHistoryUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@Rollback
class PurchaseHistoryRepositoryIntegTest {

	@Autowired
	private PurchaseHistoryRepository purchaseHistoryRepository;

	@Test
	void allowFindByPurchaseId(){
		//Arrange
		Long purchaseId = 100000L;
		PurchaseHistory ph1 = PurchaseHistoryUtils.createFakePurchaseHistory(purchaseId, "AGUARDANDO PAGAMENTO");
		PurchaseHistory ph2 = PurchaseHistoryUtils.createFakePurchaseHistory(purchaseId, "PAGO");

		//Act
		PurchaseHistory savedPh1 = purchaseHistoryRepository.save(ph1);
		PurchaseHistory savedPh2 = purchaseHistoryRepository.save(ph2);
		List<PurchaseHistory> purchaseHistory = List.of(savedPh1, savedPh2);

		List<PurchaseHistory> findHistory = purchaseHistoryRepository.findByPurchaseId(purchaseId);

		//Assert
		assertThat(findHistory).isNotNull();
		assertThat(findHistory.get(0).getStatus()).isEqualTo(purchaseHistory.get(0).getStatus());
		assertThat(findHistory.get(1).getStatus()).isEqualTo(purchaseHistory.get(1).getStatus());
	}

	@Test
	void allowCreatePurchaseHistory(){
		//Arrange
		PurchaseHistory ph1 = PurchaseHistoryUtils.createFakePurchaseHistory(1L, "AGUARDANDO PAGAMENTO");

		//Act
		PurchaseHistory newHistory = purchaseHistoryRepository.save(ph1);

		//Assert
		assertThat(newHistory).isNotNull().isEqualTo(ph1);
	}
}
