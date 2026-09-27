package io.github.olly_org.olly;

import org.springframework.boot.SpringApplication;

public class TestOllyApplication {

	public static void main(String[] args) {
		SpringApplication.from(OllyApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
