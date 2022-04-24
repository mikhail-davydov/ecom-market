package com.mkhldvdv.ecommarket.aspect;

import com.mkhldvdv.ecommarket.config.ThrottlingRequestsConfig;
import com.mkhldvdv.ecommarket.exception.ThrottlingException;
import com.mkhldvdv.ecommarket.model.Pointer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mkhldvdv.ecommarket.utils.Constants.*;

@Aspect
@Slf4j
@RequiredArgsConstructor
@Component
public class ThrottlingAspect {

    private static final String TOO_MANY_REQUESTS_ERROR_MESSAGE_TEMPLATE =
            "Too many requests from IP: %s. Allowed rate for [%s]: %d requests per %d minutes";

    private final ThrottlingRequestsConfig throttlingRequestsConfig;
    private final Map<String, List<Long>> cache;
    private final Map<String, Pointer> pointers;

    @Before("@annotation(throttling)")
    public void checkOnThrottling(JoinPoint.StaticPart joinPoint, Throttling throttling) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String remoteIP = request.getRemoteAddr();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String cacheKey = String.format("%s/%s/%s", remoteIP, className, methodName);

        log.debug("cacheKey: {}", cacheKey);

        int capacity = throttlingRequestsConfig.getSettings().get(REQUESTS_COUNT);
        int retention = throttlingRequestsConfig.getSettings().get(RETENTION_PERIOD_MINUTES);

        if (customThrottlingSettingsExist(throttling)) {
            log.debug("Custom Throttling Settings for [{}]: requestCount {}, retentionPeriodInMinutes {}",
                    String.format("%s/%s", className, methodName),
                    throttling.requestCount(),
                    throttling.retentionPeriodInMinutes()
            );
            capacity = (throttling.requestCount() > 0) ? throttling.requestCount() : capacity;
            retention = (throttling.retentionPeriodInMinutes() > 0) ? throttling.retentionPeriodInMinutes() : retention;
        }

        if (!isCacheKeyInitialized(cacheKey)) {
            initializeCacheKey(cacheKey, capacity);
        }

        if (!isRequestAvailableToProceed(cacheKey, capacity, retention)) {
            String errorMessage = String.format(TOO_MANY_REQUESTS_ERROR_MESSAGE_TEMPLATE, remoteIP, cacheKey, capacity, retention);
            log.warn(errorMessage);
            throw new ThrottlingException(errorMessage);
        }

    }

    private boolean isRequestAvailableToProceed(String cacheKey, int capacity, int retention) {
        List<Long> timeEntries = cache.get(cacheKey);
        Pointer pointer = pointers.get(cacheKey);

        log.debug("[{}] current pointers: {}", cacheKey, pointer);

        int readIndex = pointer.getRead() % capacity;
        int writeIndex = pointer.getWrite() % capacity;
        long currentTime = System.currentTimeMillis();
        long retentionMillis = retention * ONE_MINUTE_MILLIS;
        Long oldest = timeEntries.get(readIndex);

        if (Objects.isNull(oldest)) {
            timeEntries.set(writeIndex, currentTime);
            pointer.setWrite(++writeIndex);
            return true;
        }

        if (isRetentionExpired(currentTime, oldest, retentionMillis)) {
            timeEntries.set(writeIndex, currentTime);
            pointer.setRead(++readIndex);
            return true;
        }

        if (isAvailableSlotExist(readIndex, writeIndex)) {
            timeEntries.set(writeIndex, currentTime);
            pointer.setWrite(++writeIndex);
            return true;
        }

        return false;
    }

    private boolean isAvailableSlotExist(int readIndex, int writeIndex) {
        return readIndex != writeIndex;
    }

    private boolean isRetentionExpired(long currentTime, Long oldest, long retentionMillis) {
        return currentTime - oldest - retentionMillis > 0;
    }

    private void initializeCacheKey(String cacheKey, int capacity) {
        cache.put(cacheKey, Arrays.asList(new Long[capacity]));
        pointers.put(cacheKey, new Pointer());
    }

    private boolean isCacheKeyInitialized(String cacheKey) {
        return cache.containsKey(cacheKey);
    }

    private boolean customThrottlingSettingsExist(Throttling throttling) {
        return throttling.requestCount() > 0 || throttling.retentionPeriodInMinutes() > 0;
    }

}
