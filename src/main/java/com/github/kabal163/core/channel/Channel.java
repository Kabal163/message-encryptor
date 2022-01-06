package com.github.kabal163.core.channel;

import javax.annotation.Nullable;

/**
 * Represents a channel of items.
 * Items must be ordered by their insertion order
 *
 * @param <T> item's type
 * @see ChannelsHolder
 */
public interface Channel<T> {

    /**
     * Adds a new item into the current channel.
     * Operation is not blocking.
     *
     * @param item to be added to the channel
     */
    void addItem(T item);

    /**
     * Adds a new item into the current channel.
     * Attempts to put a response into the full
     * channel will result in the operation blocking.
     *
     * @param item to be added to the channel
     * @throws InterruptedException may occur while waiting
     *                              the operation execution
     *                              over the full channel
     */
    void putItem(T item) throws InterruptedException;

    /**
     * Removes and returns an item from the channel.
     * If there is no items in the channel then {@code null}
     * will be returned. Operation is not blocking.
     *
     * @return a channel's item
     */
    @Nullable
    T pollItem();

    /**
     * Removes and returns an item from the channel. Attempts
     * to take an item from the empty channel will result in the
     * operation blocking.
     *
     * @return a channel's item
     * @throws InterruptedException may occur while waiting an item
     *                              from the empty channel
     */
    T takeItem() throws InterruptedException;
}
