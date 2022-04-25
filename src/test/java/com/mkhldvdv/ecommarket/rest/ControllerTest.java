package com.mkhldvdv.ecommarket.rest;

import com.mkhldvdv.ecommarket.config.ThrottlingRequestsConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mkhldvdv.ecommarket.utils.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ThrottlingRequestsConfig throttlingRequestsConfig;

    @Test
    void runWithConcurrency() throws InterruptedException {
        int ipCount = 15;
        int successExpected = throttlingRequestsConfig.getSettings().get(REQUESTS_COUNT);
        int numberOfThreads = ipCount * successExpected;
        int delay = throttlingRequestsConfig.getSettings().get(RETENTION_PERIOD_MINUTES);

        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);


        // first run, 3 of each succeeded
        Map<String, AtomicInteger> ipCounters = new ConcurrentHashMap<>(ipCount);
        runTest(numberOfThreads, ipCount, ipCounters, service, latch);
        ipCounters.values().forEach(ipCounter -> assertEquals(successExpected, ipCounter.get()));

        // second run, none succeeded
        ipCounters = new ConcurrentHashMap<>(ipCount);
        runTest(numberOfThreads, ipCount, ipCounters, service, latch);
        int noSuccess = 0;
        ipCounters.values().forEach(ipCounter -> assertEquals(noSuccess, ipCounter.get()));

        Thread.sleep(delay + ONE_MINUTE_MILLIS);

        // third run after retention period delay, 3 of each succeeded
        ipCounters = new ConcurrentHashMap<>(ipCount);
        runTest(numberOfThreads, ipCount, ipCounters, service, latch);
        ipCounters.values().forEach(ipCounter -> assertEquals(successExpected, ipCounter.get()));

    }

    private void runTest(int numberOfThreads, int ipCount, Map<String, AtomicInteger> counters, ExecutorService service, CountDownLatch latch) throws InterruptedException {
        for (int i = 0; i < numberOfThreads; i++) {
            int currentIndex = i % ipCount;
            String ipAddress = "0.0.0." + currentIndex;
            service.execute(() -> {
                try {
                    MvcResult result = mockMvc.perform(get("/")
                            .with(request -> {
                                request.setRemoteAddr(ipAddress);
                                return request;
                            })).andReturn();

                    if (result.getResponse().getStatus() == HttpStatus.OK.value()) {
                        counters.putIfAbsent(ipAddress, new AtomicInteger(0));
                        counters.get(ipAddress).getAndIncrement();
                    }

                } catch (Exception e) {
                    log.info("Error occurred in test: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

}