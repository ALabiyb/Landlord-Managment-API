package com.tz.rental.landlord_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TanzaniaLandlordManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TanzaniaLandlordManagementSystemApplication.class, args);
	}

}
