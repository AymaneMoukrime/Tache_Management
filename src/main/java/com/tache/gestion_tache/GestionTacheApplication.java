package com.tache.gestion_tache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class GestionTacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionTacheApplication.class, args);
    }

}
