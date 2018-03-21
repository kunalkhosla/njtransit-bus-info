package com.kkhosla.njtransitbuslive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.kkhosla.njtransitbuslive")
public class NjtransitBusLiveApplication {

	public static void main(String[] args) {
		System.getProperties().put("server.port", "5000" );
		//System.getProperties().put("server.contextPath", "/businfo");

		SpringApplication.run(NjtransitBusLiveApplication.class, args);
	}
}
