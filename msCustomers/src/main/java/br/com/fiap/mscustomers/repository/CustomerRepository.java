package br.com.fiap.mscustomers.repository;

import br.com.fiap.mscustomers.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
	List<Customer> findByFullNameContainingIgnoreCase(String fullName);
}
