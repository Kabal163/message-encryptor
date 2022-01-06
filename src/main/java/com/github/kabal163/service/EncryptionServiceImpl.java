package com.github.kabal163.service;

import com.github.kabal163.core.channel.ChannelsHolder;
import com.github.kabal163.exception.ResponseConsumingException;
import lombok.RequiredArgsConstructor;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
@RequiredArgsConstructor
public class EncryptionServiceImpl implements EncryptionService {

    private final ChannelsHolder channelsHolder;

    @Override
    public void produce(Request request) {
        if (request == null) throw new IllegalArgumentException("Request must not be null!");

        channelsHolder.addRequest(request);
    }

    @Override
    public Response consume() {
        try {
            return channelsHolder.getResponse();
        } catch (InterruptedException e) {
            throw new ResponseConsumingException("Error while consuming a response!", e);
        }
    }
}
