package com.finpong.quartz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringbootQuartz2024Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootQuartz2024Application.class, args);
    }

}
