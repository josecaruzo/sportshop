package br.com.fiap.msbatches.controller;

import br.com.fiap.msbatches.services.BatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batches")
public class BatchController {
	private final BatchService batchService;

	public BatchController(BatchService batchService) {
		this.batchService = batchService;
	}

	@PostMapping("/runProductBatch")
	public ResponseEntity<String> runProductBatch() {
		batchService.runProductBatch();
		return ResponseEntity.ok("O batch de produto foi executado com sucesso"); //Batch job has been invoked
	}
}
