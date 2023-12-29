package com.zerobase.commerce.api.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.zerobase.commerce"})
@EntityScan(basePackages = {"com.zerobase.commerce.database"})
@EnableJpaRepositories(basePackages = {"com.zerobase.commerce.database"})
public class JpaConfig {
}
