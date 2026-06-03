package com.crushVers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.crushVers", "com.crushVers.login"})
public class CrushVerseApp {

    public static void main(String[] args) {
        SpringApplication.run(CrushVerseApp.class, args);
        //перенести в норм логи
        System.out.println(" CrushVerseApp запущен! ");
        System.out.println(" Откройте: http://localhost:8080 ");
    }
}