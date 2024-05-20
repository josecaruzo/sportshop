package br.com.fiap.msstock.repository;

import br.com.fiap.msstock.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByNameEqualsIgnoreCase(String name);
	List<Product> findByNameContainingIgnoreCase(String name);
}
