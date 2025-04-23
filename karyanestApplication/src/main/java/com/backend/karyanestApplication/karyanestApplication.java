package com.backend.karyanestApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.example.rbac",
		"com.example.module_b",
		"com.example.Authentication"
	        "com.example.karyanestApplication"
})
public class karyanestApplication {
	public static void main(String[] args) {
		SpringApplication.run(karyanestApplication.class, args);
	}
}
