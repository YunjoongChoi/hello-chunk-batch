package com.choimory.hellochunkbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class HelloChunkBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloChunkBatchApplication.class, args);
    }

}
