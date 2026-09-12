package com.wallet.wallet_service.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class RedisConfig {
    // Basic Redis caching enabled via Spring Boot auto-configuration
}
