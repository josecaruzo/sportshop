package br.com.fiap.msstock.functions;

import br.com.fiap.msstock.entity.Product;
import br.com.fiap.msstock.service.StockService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class ProductConsumers {
	private final StockService stockService;

	public ProductConsumers(StockService customerService) {
		this.stockService = customerService;
	}

	@Bean(name = "updateStock")
	Consumer<Product> updateStock(){
		return stockService::updateStock;
	}
}
