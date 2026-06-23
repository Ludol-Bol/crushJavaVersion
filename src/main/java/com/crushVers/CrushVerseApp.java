package com.crushVers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@ComponentScan(basePackages = {"com.crushVers", "com.crushVers.login"})

public class CrushVerseApp {
    private static final Logger log = LoggerFactory.getLogger(CrushVerseApp.class);

    public static void main(String[] args) {
        SpringApplication.run(CrushVerseApp.class, args);
        log.info(" CrushVerseApp запущен! ");
        log.info(" Откройте: http://localhost:8080 ");
    }
}