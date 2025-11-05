package com.fastcampus.ecommerce;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class WebEcommerceApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory("./")              // Root project
				.ignoreIfMissing()            // Tidak crash jika .env tidak ada
				.systemProperties()           // Load ke System properties
				.load();

		SpringApplication.run(WebEcommerceApplication.class, args);
	}

}
