package com.github.kabal163;

import com.github.kabal163.config.Config;
import com.github.kabal163.core.ProcessorManager;
import com.github.kabal163.core.channel.ChannelsHolder;
import com.github.kabal163.core.channel.ChannelsHolderImpl;
import com.github.kabal163.service.EncryptionService;
import com.github.kabal163.service.EncryptionServiceImpl;
import com.github.kabal163.service.Request;
import com.github.kabal163.service.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Application {

    private static final int CONSUMERS_NUMBER = 7;
    private static final int PRODUCERS_NUMBER = 2;
    private static final int MIN_PRIORITY = 1;
    private static final int MAX_PRIORITY = 10;

    public static void main(String[] args) {
        Config config = new Config();
        config.setProcessorsPoolSize(10);
        config.setResponseChannelPoolSize(100000);
        ChannelsHolder channelsHolder = new ChannelsHolderImpl(config);
        EncryptionService encryptionService = new EncryptionServiceImpl(channelsHolder);

        initProcessors(channelsHolder, config);
        initConsumers(encryptionService);
        initProducers(encryptionService);
    }

    private static void initProcessors(ChannelsHolder channelsHolder, Config config) {
        ProcessorManager.start(channelsHolder, config);
    }

    private static void initProducers(EncryptionService encryptionService) {
        ExecutorService pool = Executors.newCachedThreadPool();
        Random random = new Random();
        for (int i = 0; i < PRODUCERS_NUMBER; i++) {
            pool.execute(() -> {
                while (true) {
                    encryptionService.produce(
                            Request.builder()
                                    .priority(random.nextInt(MAX_PRIORITY) + MIN_PRIORITY)
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
                    Response response = encryptionService.consume();
                    log.info("Read a response: {}", response);
                }
            });
        }
    }
}
