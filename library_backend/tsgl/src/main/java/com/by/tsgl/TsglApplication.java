package com.by.tsgl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class TsglApplication {

    public static void main(String[] args) {
        SpringApplication.run(TsglApplication.class, args);
    }

}
