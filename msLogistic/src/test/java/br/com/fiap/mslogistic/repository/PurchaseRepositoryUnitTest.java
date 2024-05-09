package br.com.fiap.mslogistic.repository;

import br.com.fiap.mslogistic.entity.Purchase;
import br.com.fiap.mslogistic.utils.PurchaseUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PurchaseRepositoryUnitTest {

	@Mock
	private PurchaseRepository purchaseRepository;

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
	void allowFindById(){
		//Arrange
		Long purchaseId = 1L;
		Purchase purchase = PurchaseUtils.createFakePurchase(purchaseId);
		when(purchaseRepository.findById(any(Long.class))).thenReturn(Optional.of(purchase));

		//Act
		Optional<Purchase> findPurchase = purchaseRepository.findById(purchaseId);

		//Assert
		assertThat(findPurchase).isNotNull().isEqualTo(Optional.of(purchase));
		verify(purchaseRepository, times(1)).findById(any(Long.class));
	}

	@Test
	void allowFindByStatusOrderByDeliveryZipCode(){
		//Arrange
		String status = "PAGO"; //Initial status
		Purchase p1 = PurchaseUtils.createFakePurchase(1L, status);
		Purchase p2 = PurchaseUtils.createFakePurchase(2L, status);
		List<Purchase> purchaseList = List.of(p1, p2);
		when(purchaseRepository.findByStatusOrderByDeliveryZipCode(any(String.class))).thenReturn(purchaseList);

		//Act
		List<Purchase> purchases = purchaseRepository.findByStatusOrderByDeliveryZipCode(status);

		//Assert
		assertThat(purchases).isNotNull().isEqualTo(purchaseList);
		verify(purchaseRepository, times(1)).findByStatusOrderByDeliveryZipCode(any(String.class));
	}

	@Test
	void allowSavePurchase(){
		//Arrange
		Purchase purchase = PurchaseUtils.createFakePurchase(1L);
		when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

		//Act
		Purchase newPurchase = purchaseRepository.save(purchase);

		//Assert
		assertThat(newPurchase).isNotNull().isEqualTo(purchase);
		verify(purchaseRepository, times(1)).save(any(Purchase.class));
	}
}
