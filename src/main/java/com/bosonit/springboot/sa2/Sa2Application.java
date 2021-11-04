package com.bosonit.springboot.sa2;

import com.bosonit.springboot.sa2.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Sa2Application {

	public static void main(String[] args) {
		SpringApplication.run(Sa2Application.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			if(args.length > 0)
				storageService.setPath(args[0]);
			else
				//storageService.setPath(System.getProperty("user.dir"));
				storageService.setPath("default");
			storageService.deleteAll();
			storageService.init();
		};
	}

}
