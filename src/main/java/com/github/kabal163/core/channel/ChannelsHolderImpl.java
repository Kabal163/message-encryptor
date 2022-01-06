package com.github.kabal163.core.channel;

import com.github.kabal163.config.Config;
import com.github.kabal163.service.Request;
import com.github.kabal163.service.Response;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingQueue;

@ThreadSafe
public class ChannelsHolderImpl implements ChannelsHolder {

    private final ConcurrentSkipListMap<Integer, PriorityChannel<Request>> requestChannels;
    private final Channel<Response> responseChannel;

    public ChannelsHolderImpl(Config config) {
        this.requestChannels = new ConcurrentSkipListMap<>(Comparator.reverseOrder());
        this.responseChannel = new ChannelImpl<>(new ArrayBlockingQueue<>(config.getResponseChannelPoolSize()));
    }

    @Override
    public void addRequest(Request request) {
        requestChannels.compute(
                request.getPriority(),
                (p, channel) -> {
                    if (channel == null) {
                        channel = new PriorityChannelImpl<>(
                                request.getPriority(),
                                new LinkedBlockingQueue<>());
                    }
                    channel.addItem(request);
                    return channel;
                });
    }

    @Override
    public void putResponse(Response response) throws InterruptedException {
        responseChannel.putItem(response);
    }

    @Override
    public Iterator<PriorityChannel<Request>> getRequestChannelsIterator() {
        return requestChannels.values().iterator();
    }

    @Override
    public boolean removeChannel(PriorityChannel<Request> channel) {
        return requestChannels.remove(channel.getPriority(), channel);
    }

    @Override
    public Response getResponse() throws InterruptedException {
        return responseChannel.takeItem();
    }
}
