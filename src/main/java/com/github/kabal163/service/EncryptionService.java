package com.github.kabal163.service;

import com.github.kabal163.exception.ResponseConsumingException;

/**
 * Provides an ability to encrypt a {@link Request#getPayload() payload}.
 * Implementations must be thread safe.
 */
public interface EncryptionService {

    /**
     * Queues a {@link Request} for the further encryption.
     *
     * @param request contains payload to be encrypted
     * @throws IllegalArgumentException if {@code request} is null
     */
    void produce(Request request);

    /**
     * @return response with encrypted payload
     * @throws ResponseConsumingException if an exception occurred
     *                                    while trying to consume
     *                                    a {@link Response}
     */
    Response consume();
}
