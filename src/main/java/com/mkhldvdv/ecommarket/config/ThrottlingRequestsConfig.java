package com.mkhldvdv.ecommarket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "throttling.requests")
public class ThrottlingRequestsConfig {

    private Map<String, Integer> settings = new HashMap<>();

}
