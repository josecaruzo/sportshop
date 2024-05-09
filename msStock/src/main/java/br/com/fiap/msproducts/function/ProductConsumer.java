package br.com.fiap.msproducts.function;

import br.com.fiap.msproducts.entity.Product;
import br.com.fiap.msproducts.service.StockService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class ProductConsumer {
	private final StockService stockService;

	public ProductConsumer(StockService customerService) {
		this.stockService = customerService;
	}

	@Bean(name = "removeStock")
	Consumer<Product> removeStockConsumer(){
		return stockService::removeStock;
	}
}
