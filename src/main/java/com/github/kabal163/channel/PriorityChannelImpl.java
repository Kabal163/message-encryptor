package com.github.kabal163.channel;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

@Immutable
@ThreadSafe
public class PriorityChannelImpl<T> extends ChannelImpl<T> implements PriorityChannel<T> {

    private final int priority;

    public PriorityChannelImpl(int priority, BlockingQueue<T> queue) {
        super(queue);
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriorityChannelImpl<?> another = (PriorityChannelImpl<?>) o;
        return priority == another.priority && super.queue.isEmpty() == another.queue.isEmpty();
    }

    @Override
    public int hashCode() {
        return Objects.hash(priority, super.queue.isEmpty());
    }
}
