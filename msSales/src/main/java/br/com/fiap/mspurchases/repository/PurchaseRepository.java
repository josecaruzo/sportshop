package br.com.fiap.mspurchases.repository;

import br.com.fiap.mspurchases.entity.Purchase;
import br.com.fiap.mspurchases.entity.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long>{
	List<Purchase> findByStatusIgnoreCase(String status);
}
