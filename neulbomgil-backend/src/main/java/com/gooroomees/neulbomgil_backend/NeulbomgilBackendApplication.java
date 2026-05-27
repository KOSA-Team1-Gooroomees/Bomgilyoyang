package com.gooroomees.neulbomgil_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing   //이게 없으면 createdAt이 계속 null
public class NeulbomgilBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(NeulbomgilBackendApplication.class, args);
	}

}
