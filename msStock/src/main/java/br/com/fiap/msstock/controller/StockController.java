package br.com.fiap.msstock.controller;

import br.com.fiap.msstock.entity.Product;
import br.com.fiap.msstock.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock")
public class StockController {

	private final StockService stockService;

	public StockController(StockService stockService) {
		this.stockService = stockService;
	}

	@GetMapping("/getProductById/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable Long id) {
		return ResponseEntity.ok(this.stockService.getProductById(id));
	}

	@GetMapping("/getProductsByName/{name}")
	public ResponseEntity<List<Product>> getProductsByName(@PathVariable String name) {
		return ResponseEntity.ok(this.stockService.getProductsByName(name));
	}

	@PostMapping("/createProduct")
	public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.stockService.createProduct(product));
	}

	@PutMapping("/updateProduct/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody @Valid Product product) {
		return ResponseEntity.ok(this.stockService.updateProduct(id, product));
	}

	@DeleteMapping("/deleteProduct/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
		return ResponseEntity.ok(this.stockService.deleteProduct(id));
	}
}
