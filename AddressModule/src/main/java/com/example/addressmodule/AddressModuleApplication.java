
package com.example.addressmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories// Comment out this annotation if you are running from karyanestApplication
public class AddressModuleApplication {
	public static void main(String[] args) {
		 SpringApplication.run(AddressModuleApplication.class, args); // Comment out this line if you are running from karyanestApplication
	}
}

