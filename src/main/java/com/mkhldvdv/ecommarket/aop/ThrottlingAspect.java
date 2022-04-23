package com.mkhldvdv.ecommarket.aop;

import com.github.benmanes.caffeine.cache.Cache;
import com.mkhldvdv.ecommarket.config.ThrottlingRequestsConfig;
import com.mkhldvdv.ecommarket.exception.ThrottlingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Slf4j
@RequiredArgsConstructor
@Component
public class ThrottlingAspect {

    private final Cache<String, String> cache;
    private final ThrottlingRequestsConfig throttlingRequestsConfig;

    @Before("@annotation(throttling)")
    public void checkOnThrottling(Throttling throttling) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String errorMessage = String.format("Too many requests from IP: %s", request.getRemoteAddr());
        log.warn(errorMessage);
        throw new ThrottlingException(errorMessage);

    }

}
