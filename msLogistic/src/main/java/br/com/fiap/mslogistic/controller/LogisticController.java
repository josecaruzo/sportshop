package br.com.fiap.mslogistic.controller;

import br.com.fiap.mslogistic.entity.Purchase;
import br.com.fiap.mslogistic.entity.PurchaseHistory;
import br.com.fiap.mslogistic.service.LogisticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logistic")
public class LogisticController {
	private final LogisticService logisticService;

	public LogisticController(LogisticService logisticService) {
		this.logisticService = logisticService;
	}

	@GetMapping("/getHistoryByPurchaseId/{purchaseId}")
	public ResponseEntity<List<PurchaseHistory>> getHistoryByPurchaseId(@PathVariable Long purchaseId){
		return ResponseEntity.ok(this.logisticService.getHistoryByPurchaseId(purchaseId));
	}

	@PutMapping("/dispatchPurchases")
	public ResponseEntity<List<Purchase>> dispatchPurchases(){
		return ResponseEntity.ok(this.logisticService.dispatchPurchases());
	}

	@PutMapping("/deliveryPurchase/{purchaseId}")
	public ResponseEntity<String> deliveryPurchase(@PathVariable Long purchaseId){
		return ResponseEntity.ok(this.logisticService.deliveryPurchase(purchaseId));
	}

}
