package com.github.kabal163.core;

import com.github.kabal163.core.channel.ChannelsHolder;
import com.github.kabal163.config.Config;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Slf4j
@ThreadSafe
public class ProcessorManager {

    private static volatile boolean active;
    private static ExecutorService pool;
    private static final ThreadFactory THREAD_FACTORY = (r) -> new Thread(r, "processor-thread");

    public static synchronized boolean start(ChannelsHolder channelsHolder, Config config) {
        if (channelsHolder == null) throw new IllegalArgumentException("Channels holder must not be null!");
        if (config == null) throw new IllegalArgumentException("config holder must not be null!");

        if (active) {
            return false;
        }

        pool = Executors.newFixedThreadPool(config.getProcessorsPoolSize(), THREAD_FACTORY);
        for (int i = 0; i < config.getProcessorsPoolSize(); i++) {
            pool.execute(new Processor(channelsHolder));
        }
        active = true;

        return true;
    }

    public static synchronized boolean stop() {
        if (!active) {
            return false;
        }

        pool.shutdownNow();
        active = false;

        log.info("Processing has been shut down");

        return true;
    }
}
