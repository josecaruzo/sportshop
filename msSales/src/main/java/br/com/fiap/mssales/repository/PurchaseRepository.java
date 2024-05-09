package br.com.fiap.mssales.repository;

import br.com.fiap.mssales.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long>{
	List<Purchase> findByStatusIgnoreCase(String status);
}
