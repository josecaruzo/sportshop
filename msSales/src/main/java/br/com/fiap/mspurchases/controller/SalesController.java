package br.com.fiap.mspurchases.controller;

import br.com.fiap.mspurchases.entity.Purchase;
import br.com.fiap.mspurchases.service.SalesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales")
public class SalesController {
	private final SalesService salesService;

	public SalesController(SalesService salesService) {
		this.salesService = salesService;
	}

	@GetMapping("/getPurchaseById/{id}")
	public ResponseEntity<Purchase> getPurchaseById(@PathVariable Long id) {
		return ResponseEntity.ok(this.salesService.getPurchaseById(id));
	}

	@GetMapping("/getPurchasesByStatus/{status}")
	public ResponseEntity<List<Purchase>> getPurchasesByStatus(@PathVariable String status) {
		return ResponseEntity.ok(this.salesService.getPurchasesByStatus(status));
	}

	@PostMapping("/createPurchase")
	public ResponseEntity<Purchase> createPurchase(@RequestBody @Valid Purchase purchase) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.salesService.createPurchase(purchase));
	}

	@PutMapping("/payPurchase/id/{id}")
	public ResponseEntity<Purchase> payPurchase(@PathVariable Long id) {
		return ResponseEntity.ok(this.salesService.payPurchase(id));
	}

	@PutMapping("/cancelPurchase/id/{id}")
	public ResponseEntity<Purchase> cancelPurchase(@PathVariable Long id) {
		return ResponseEntity.ok(this.salesService.cancelPurchase(id));
	}

}
