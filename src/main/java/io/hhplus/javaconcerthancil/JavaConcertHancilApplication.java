package io.hhplus.javaconcerthancil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableRetry
public class JavaConcertHancilApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaConcertHancilApplication.class, args);
	}

}
