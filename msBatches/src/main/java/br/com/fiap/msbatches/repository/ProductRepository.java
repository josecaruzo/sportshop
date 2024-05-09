package br.com.fiap.msbatches.repository;

import br.com.fiap.msbatches.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	Optional<Product> findByName(String name);
}
