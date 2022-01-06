package com.github.kabal163.channel;

public interface PriorityChannel<T> extends Channel<T> {

    int getPriority();
}
