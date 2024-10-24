package io.hhplus.javaconcerthancil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JavaConcertHancilApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaConcertHancilApplication.class, args);
	}

}
