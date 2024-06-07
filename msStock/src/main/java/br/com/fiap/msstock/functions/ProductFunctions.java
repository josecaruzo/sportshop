package br.com.fiap.msstock.functions;

import br.com.fiap.msstock.entity.Product;
import br.com.fiap.msstock.service.StockService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ProductFunctions {
	private final StockService stockService;

	public ProductFunctions(StockService customerService) {
		this.stockService = customerService;
	}

	@Bean(name = "findProduct")
	Function<Long, Product> findProduct(){
		return stockService::getProductById;
	}
}
