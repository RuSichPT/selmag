package com.github.rusichpt.managerapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ManagerAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagerAppApplication.class, args);
	}

}
