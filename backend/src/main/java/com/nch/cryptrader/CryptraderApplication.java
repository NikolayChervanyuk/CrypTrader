package com.nch.cryptrader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.nch.cryptrader.configuration")
public class CryptraderApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptraderApplication.class, args);
	}

}
