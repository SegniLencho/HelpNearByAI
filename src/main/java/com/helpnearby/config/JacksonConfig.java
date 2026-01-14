package com.helpnearby.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;


@Configuration
public class JacksonConfig {

    @PostConstruct
    public void init() {
        System.out.println("========================================");
        System.out.println("✅ JacksonConfig is being loaded!");
        System.out.println("========================================");
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        System.out.println("✅ Creating Primary ObjectMapper bean with JavaTimeModule");
        
        ObjectMapper mapper = new ObjectMapper();
        
        // Register Java 8 date/time module
        
        mapper.registerModule(new JavaTimeModule());
        System.out.println("✅ JavaTimeModule registered");
        
        // Disable writing dates as timestamps (use ISO-8601 format instead)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        System.out.println("✅ WRITE_DATES_AS_TIMESTAMPS disabled");
        
        return mapper;
    }
    
    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        System.out.println("✅ Creating Jackson2ObjectMapperBuilder");
        
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.modules(new JavaTimeModule());
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return builder;
    }
}