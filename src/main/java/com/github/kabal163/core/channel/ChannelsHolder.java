package com.github.kabal163.core.channel;

import com.github.kabal163.service.Request;
import com.github.kabal163.service.Response;

import java.util.Iterator;

/**
 * Entrypoint to work with the {@link Channel channels}.
 * Provides ability to store and manage the channels.
 * Implementations must be thread safe.
 *
 * @see Channel
 * @see PriorityChannel
 */
public interface ChannelsHolder {

    /**
     * Adds a {@code request} to a request channel with the appropriate
     * priority. If there is no such channel the new one will be
     * created.
     *
     * @param request contains payload to be encrypted
     * @throws IllegalArgumentException if the {@code request} is null
     */
    void addRequest(Request request);

    /**
     * Adds a {@code response} to a response channel in order
     * to get the response in the future. Attempts to put a response
     * into the full response channel will result in the operation blocking.
     *
     * @param response contains encrypted payload
     * @throws InterruptedException     may occur while waiting the operation
     *                                  execution over the full response channel
     * @throws IllegalArgumentException if the {@code response} is null
     * @see #getResponse()
     */
    void putResponse(Response response) throws InterruptedException;

    /**
     * Returns a fail-safe request channels iterator.
     * An order of channels is not guarantied.
     *
     * @return request channels iterator
     */
    Iterator<PriorityChannel<Request>> getRequestChannelsIterator();

    /**
     * Removes a {@code channel} from the current channels holder.
     *
     * @param channel to be removed
     * @return {@code true} if the channel has been successfully
     * removed, otherwise {@code false}
     * @throws IllegalArgumentException if the {@code request} is null
     */
    boolean removeChannel(PriorityChannel<Request> channel);

    /**
     * Returns a {@code response} from a response channel. Attempts to take
     * a response from the empty response channel will result in the operation
     * blocking.
     *
     * @return response with encrypted payload
     * @throws InterruptedException may occur while waiting a response
     *                              from an empty response channel
     * @see #putResponse(Response)
     */
    Response getResponse() throws InterruptedException;
}
