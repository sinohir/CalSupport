package com.example.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class CalSupportApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalSupportApplication.class, args);
	}

//-------------For war file--------------------------------------
/*
	@SpringBootApplication
	public class CalSupportApplication extends SpringBootServletInitializer {
	
		public static void main(String[] args) {
			SpringApplication.run(CalSupportApplication.class, args);
		}
	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder application) {
		return application.sources(CalSupportApplication.class);
	}
	*/
}
