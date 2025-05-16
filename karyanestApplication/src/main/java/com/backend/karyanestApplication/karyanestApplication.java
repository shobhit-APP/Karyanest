
package com.backend.karyanestApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {// exact case-sensitive path
		"com.example.Authentication",
		"com.example.Authentication.Controller",
		"com.example.rbac",
		"com.backend.karyanestApplication",
		"com.example.module_b",
		"com.example.notification",
		"com.example.storageService",
		"com.example.addressmodule"
})
@EnableJpaRepositories
public class karyanestApplication {

	public static void main(String[] args) {
		SpringApplication.run(karyanestApplication.class, args);
	}
}
