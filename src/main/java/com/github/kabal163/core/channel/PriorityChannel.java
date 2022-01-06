package com.github.kabal163.core.channel;

/**
 * Represent a channel with a priority
 *
 * @param <T> item's type
 */
public interface PriorityChannel<T> extends Channel<T> {

    /**
     * @return channel's priority
     */
    int getPriority();
}
