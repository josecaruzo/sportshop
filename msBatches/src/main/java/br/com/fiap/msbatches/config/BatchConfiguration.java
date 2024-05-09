package br.com.fiap.msbatches.config;

import br.com.fiap.msbatches.entity.Product;
import br.com.fiap.msbatches.services.ProductProcessor;
import br.com.fiap.msbatches.repository.ProductRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@Transactional
public class BatchConfiguration {

	@Bean
	public Job productProcessor(JobRepository jobRepository, Step fileProcessor) {
		return new JobBuilder("productProcessor", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(fileProcessor)
				.build();
	}

	@Bean
	public Step fileProcessor(JobRepository jobRepository,
			PlatformTransactionManager transactionManager,
			ItemReader<Product> itemReader,
			ItemProcessor<Product, Product> itemProcessor,
			ItemWriter<Product> itemWriter) {
		return new StepBuilder("fileProcessor", jobRepository)
				.<Product, Product>chunk(50, transactionManager)
				.taskExecutor(new SimpleAsyncTaskExecutor())
				.reader(itemReader)
				.processor(itemProcessor)
				.writer(itemWriter)
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public ItemReader<Product> itemReader() throws IOException {
		BeanWrapperFieldSetMapper<Product> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Product.class);

		return new FlatFileItemReaderBuilder<Product>()
				.name("productItemReader")
				.resource(new ClassPathResource("filesource/products.csv"))
				.delimited()
				.delimiter(";")
				.names("name", "description", "price", "quantity")
				.fieldSetMapper(fieldSetMapper)
				.build();
	}

	@Bean
	public ItemProcessor<Product, Product> itemProcessor(ProductRepository productRepository){
		return new ProductProcessor(productRepository);
	}

	@Bean
	public ItemWriter<Product> itemWriter(DataSource dataSource){
		return new JdbcBatchItemWriterBuilder<Product>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.dataSource(dataSource)
				.sql("UPDATE products SET name = :name, description = :description, price = :price, quantity = :quantity WHERE id = :id")
				.build();
	}
}
