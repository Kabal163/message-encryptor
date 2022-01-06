package com.github.kabal163.channel;

import com.github.kabal163.service.Request;
import com.github.kabal163.service.Response;

import java.util.Iterator;

public interface ChannelsHolder {

    void addRequest(Request request);

    void putResponse(Response response) throws InterruptedException;

    Iterator<PriorityChannel<Request>> getRequestChannelsIterator();

    boolean removeChannel(PriorityChannel<Request> channel);

    Response getResponse() throws InterruptedException;
}
