package com.ucamp.greenmap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GreenmapApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreenmapApplication.class, args);
	}

}
