package com.stockmarket.stock_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableScheduling
@EnableMongoRepositories(basePackages = {"com.stockmarket"})
@ComponentScan(basePackages = {"com.stockmarket"})
@SpringBootApplication
@EnableSwagger2
public class StockManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockManagementApplication.class, args);
	}

}
