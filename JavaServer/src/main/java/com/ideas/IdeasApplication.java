package com.ideas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

@SpringBootApplication
public class IdeasApplication {

	@Bean
    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
            return new OpenEntityManagerInViewFilter();
    }
	
	public static void main(String[] args) {
		SpringApplication.run(IdeasApplication.class, args);
	}
}
