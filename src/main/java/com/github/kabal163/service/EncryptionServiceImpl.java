package com.github.kabal163.service;

import com.github.kabal163.core.channel.ChannelsHolder;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class EncryptionServiceImpl implements EncryptionService {

    private final ChannelsHolder channelsHolder;

    public EncryptionServiceImpl(ChannelsHolder channelsHolder) {
        this.channelsHolder = channelsHolder;
    }

    @Override
    public void produce(Request request) {
        if (request == null) throw new IllegalArgumentException("Request must not be null!");

        channelsHolder.addRequest(request);
    }

    @Override
    public Response consume() throws InterruptedException {
        return channelsHolder.getResponse();
    }
}
