package com.github.kabal163.core;

import com.github.kabal163.channel.ChannelsHolder;
import com.github.kabal163.channel.PriorityChannel;
import com.github.kabal163.service.Request;
import com.github.kabal163.service.Response;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

class Processor implements Runnable {

    private final ChannelsHolder channelsHolder;

    public Processor(ChannelsHolder channelsHolder) {
        this.channelsHolder = channelsHolder;
    }

    @Override
    @SneakyThrows
    public void run() {
        while (!Thread.interrupted()) {
            Iterator<PriorityChannel<Request>> iterator = channelsHolder.getRequestChannelsIterator();
            while (iterator.hasNext()) {
                handle(iterator.next());
            }
        }
    }

    private void handle(PriorityChannel<Request> channel) throws InterruptedException {
        for (int i = 0; i < channel.getPriority(); i++) {
            Request request = channel.pollItem();

            if (request == null) {
                channelsHolder.removeChannel(
                        new PriorityChannelImpl<>(
                                channel.getPriority(),
                                new LinkedBlockingQueue<>()));
                break;
            }

            String result = DigestUtils.sha256Hex(request.getPayload().toString());
            channelsHolder.putResponse(
                    Response.builder()
                            .request(request)
                            .payload(result)
                            .build());
        }
    }
}
