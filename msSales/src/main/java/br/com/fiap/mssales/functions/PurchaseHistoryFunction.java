package br.com.fiap.mssales.functions;

import br.com.fiap.mssales.entity.PurchaseHistory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "history", url = "http://mslogistic:8084/api")
public interface PurchaseHistoryFunction {

	@PostMapping("/consumer-saveHistory")
	void saveHistory(PurchaseHistory purchaseHistory);
}
