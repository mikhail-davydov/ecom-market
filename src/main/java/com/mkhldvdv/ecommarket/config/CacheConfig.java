package com.mkhldvdv.ecommarket.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class CacheConfig {

    @Bean
    public Cache<String, String> cache(ThrottlingRequestsConfig throttlingRequestsConfig) {
        log.info("Throttling Requests Settings: {}", throttlingRequestsConfig.getSettings());
        return Caffeine.newBuilder()
                .expireAfterWrite(throttlingRequestsConfig.getSettings().get("minutes"), TimeUnit.MINUTES)
                .build();
    }

}
