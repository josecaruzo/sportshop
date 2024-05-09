package br.com.fiap.mslogistic.repository;

import br.com.fiap.mslogistic.entity.Purchase;
import br.com.fiap.mslogistic.utils.PurchaseUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@Rollback
class PurchaseRepositoryIntegTest {
	@Autowired
	private PurchaseRepository purchaseRepository;

	@Test
	void allowFindById(){
		//Arrange
		Purchase purchase = PurchaseUtils.createFakePurchase();

		//Act
		Purchase savedPurchase = purchaseRepository.save(purchase);
		Optional<Purchase> findPurchase = purchaseRepository.findById(savedPurchase.getId());

		//Assert
		assertThat(findPurchase).isNotNull().isEqualTo(Optional.of(savedPurchase));
	}

	@Test
	void allowFindByStatusOrderByDeliveryZipCode(){
		//Arrange
		String status = "PAGO"; //Initial status
		Purchase p1 = PurchaseUtils.createFakePurchase(status);
		Purchase p2 = PurchaseUtils.createFakePurchase(status);

		//Act
		Purchase savedP1 = purchaseRepository.save(p1);
		Purchase savedP2 = purchaseRepository.save(p2);
		List<Purchase> purchaseList = List.of(savedP1, savedP2);
		List<Purchase> purchases = purchaseRepository.findByStatusOrderByDeliveryZipCode(status);

		//Assert
		assertThat(purchases).isNotNull().isEqualTo(purchaseList);
	}

	@Test
	void allowCreatePurchase(){
		//Arrange
		Purchase p1 = PurchaseUtils.createFakePurchase();

		//Act
		Purchase newPurchase = purchaseRepository.save(p1);

		//Assert
		assertThat(newPurchase).isNotNull();
		assertThat(newPurchase.getId()).isNotNull();
		assertThat(newPurchase.getCustomerCpf()).isNotNull().isEqualTo(p1.getCustomerCpf());
		assertThat(newPurchase.getDeliveryZipCode()).isNotNull().isEqualTo(p1.getDeliveryZipCode());
	}
}
