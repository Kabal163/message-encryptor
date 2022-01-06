package com.github.kabal163.core;

import com.github.kabal163.core.channel.ChannelsHolder;
import com.github.kabal163.core.channel.PriorityChannel;
import com.github.kabal163.core.channel.PriorityChannelImpl;
import com.github.kabal163.service.Request;
import com.github.kabal163.service.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@RequiredArgsConstructor
public class Processor implements Runnable {

    private final ChannelsHolder channelsHolder;

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            Iterator<PriorityChannel<Request>> iterator = channelsHolder.getRequestChannelsIterator();
            while (iterator.hasNext()) {
                try {
                    handle(iterator.next());
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        log.info("Thread has been interrupted and is about to shut down");
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
