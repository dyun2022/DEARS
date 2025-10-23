package com.app.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@ComponentScae
@EntityScan(basePackages = "com.app.project.model")
@RestController
public class DearsApplication {
	public static void main(String[] args) {
		SpringApplication.run(DearsApplication.class, args);
		System.out.println("server started successfully.");
	}

	@GetMapping
	public String Sucess() {
		return "Welcome to the project";
	}
}
