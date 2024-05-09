package br.com.fiap.mslogistic.repository;

import br.com.fiap.mslogistic.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
	List<Purchase> findByStatusOrderByDeliveryZipCode(String status);
}
