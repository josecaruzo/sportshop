package br.com.fiap.mslogistic.function;

import br.com.fiap.mslogistic.entity.PurchaseHistory;
import br.com.fiap.mslogistic.service.LogisticService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class PurchaseHistoryConsumer {
	private final LogisticService logisticService;

	public PurchaseHistoryConsumer(LogisticService logisticService) {
		this.logisticService = logisticService;
	}

	@Bean(name = "saveHistory")
	Consumer<PurchaseHistory> saveHistoryConsumer(){
		return logisticService::createPurchaseHistory;
	}
}
