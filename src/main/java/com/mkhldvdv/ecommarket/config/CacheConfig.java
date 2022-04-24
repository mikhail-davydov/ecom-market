package com.mkhldvdv.ecommarket.config;

import com.mkhldvdv.ecommarket.model.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Slf4j
public class CacheConfig {

    @Bean
    public Map<String, List<Long>> cache(ThrottlingRequestsConfig throttlingRequestsConfig) {
        log.debug("Global Throttling Requests Settings: {}", throttlingRequestsConfig.getSettings());
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<String, Pointer> pointers() {
        return new ConcurrentHashMap<>();
    }

}
