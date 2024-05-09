package br.com.fiap.mslogistic.repository;

import br.com.fiap.mslogistic.entity.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {
	List<PurchaseHistory> findByPurchaseId(Long purchaseId);
}
