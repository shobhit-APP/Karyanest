package com.backend.karyanestApplication;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@ComponentScan(basePackages = {"com.example.rbac", "com.backend.karyanestApplication", "com.example.module_b","com.example.Authentication"})
public class karyanestApplication {
	public static void main(String[] args) {
		SpringApplication.run(karyanestApplication.class, args);
	}
}