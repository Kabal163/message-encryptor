package com.github.kabal163.service;

/**
 * Provides an ability to encrypt a {@link Request#getPayload() payload}.
 */
public interface EncryptionService {

    void produce(Request request);

    Response consume() throws InterruptedException;
}
