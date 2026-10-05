package io.github.olly_org.olly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class OllyApplication {

  public static void main(String[] args) {
    SpringApplication.run(OllyApplication.class, args);
  }
}
