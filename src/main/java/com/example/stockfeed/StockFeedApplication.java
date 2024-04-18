package com.example.stockfeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StockFeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockFeedApplication.class, args);
    }

}
