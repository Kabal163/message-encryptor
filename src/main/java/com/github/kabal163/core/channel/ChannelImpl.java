package com.github.kabal163.core.channel;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.BlockingQueue;

@ThreadSafe
@ParametersAreNonnullByDefault
public class ChannelImpl<T> implements Channel<T> {

    protected final BlockingQueue<T> queue;

    public ChannelImpl(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    @Override
    public void addItem(T item) {
        this.queue.add(item);
    }

    @Override
    public void putItem(T item) throws InterruptedException {
        this.queue.put(item);
    }

    @Override
    public T takeItem() throws InterruptedException {
        return this.queue.take();
    }

    @Override
    @Nullable
    public T pollItem() {
        return this.queue.poll();
    }
}
