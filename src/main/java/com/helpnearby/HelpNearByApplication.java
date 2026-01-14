package com.helpnearby;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HelpNearByApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelpNearByApplication.class, args);
    }

}
