package com.github.kabal163.config;

import com.github.kabal163.core.Processor;
import com.github.kabal163.core.channel.Channel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config {

    /**
     * Thread pool size of processors.
     * The number of working processors is equal
     * to pool sized. It means that all thread
     * will be allocated for the working processors
     *
     * @see Processor
     */
    private int processorsPoolSize;

    /**
     * Capacity of a response channel.
     *
     * @see Channel
     */
    private int responseChannelCapacity;
}
