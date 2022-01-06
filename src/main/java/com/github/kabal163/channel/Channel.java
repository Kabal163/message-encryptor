package com.github.kabal163.channel;

public interface Channel<T> {

    void addItem(T item);

    void putItem(T item) throws InterruptedException;

    T takeItem() throws InterruptedException;

    T pollItem();
}
