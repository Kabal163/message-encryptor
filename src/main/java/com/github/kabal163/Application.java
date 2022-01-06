package com.github.kabal163;

import com.github.kabal163.channel.ChannelsHolder;
import com.github.kabal163.channel.ChannelsHolderImpl;
import com.github.kabal163.config.Config;
import com.github.kabal163.core.ProcessorManager;
import com.github.kabal163.service.EncryptionService;
import com.github.kabal163.service.EncryptionServiceImpl;
import com.github.kabal163.service.Request;
import com.github.kabal163.service.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Application {

    private static final int CONSUMERS_NUMBER = 10;
    private static final int PRODUCERS_NUMBER = 3;

    public static void main(String[] args) {
        Config config = new Config();
        config.setProcessorsPoolSize(10);
        config.setResponseChannelPoolSize(1000);
        ChannelsHolder channelsHolder = new ChannelsHolderImpl(config);
        EncryptionService encryptionService = new EncryptionServiceImpl(channelsHolder);

        initProcessors(channelsHolder, config);
        initConsumers(encryptionService);
        initProducers(encryptionService);
    }

    private static void initProcessors(ChannelsHolder channelsHolder, Config config) {
        ProcessorManager.process(channelsHolder, config);
    }

    private static void initProducers(EncryptionService encryptionService) {
        ExecutorService pool = Executors.newCachedThreadPool();
        final AtomicInteger count = new AtomicInteger(0);
        for (int i = 0; i < PRODUCERS_NUMBER; i++) {
            count.incrementAndGet();
            pool.execute(() -> {
                while (true) {
                    encryptionService.produce(
                            Request.builder()
                                    .priority(count.get())
                                    .payload(UUID.randomUUID())
                                    .build());
                }
            });
        }
    }

    private static void initConsumers(EncryptionService encryptionService) {
        ExecutorService pool = Executors.newFixedThreadPool(CONSUMERS_NUMBER);
        for (int i = 0; i < CONSUMERS_NUMBER; i++) {
            pool.execute(() -> {
                while (true) {
                    Response response = null;
                    try {
                        response = encryptionService.consume();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.info("Read a response: {}", response);
                }
            });
        }
    }
}
