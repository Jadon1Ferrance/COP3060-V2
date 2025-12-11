package edu.famu.cop3060.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResourcesApplication {
  private static final Logger log = LoggerFactory.getLogger(ResourcesApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(ResourcesApplication.class, args);
    log.info("PA03 — Campus Resources API started (Jadon Ferrance, FAMU CS ’25).");
  }
}
