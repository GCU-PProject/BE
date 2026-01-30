package com.glow.Glaw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GlawApplication {

	public static void main(String[] args) {
		SpringApplication.run(GlawApplication.class, args);
	}

}
