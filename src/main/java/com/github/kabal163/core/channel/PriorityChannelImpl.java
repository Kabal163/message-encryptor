package com.github.kabal163.core.channel;

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

    /**
     * @apiNote
     * In order to check equality of the two {@link PriorityChannelImpl channels}
     * we consider only two attributes - priority and channel's emptiness.
     * We consider that if two channels have the same priority, and they are both
     * empty or both not empty then they are equal. We don't consider channel's
     * content. This logic is using while removing empty channels from {@link ChannelsHolder}.
     * Keep it in mind when you call this method.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriorityChannelImpl<?> another = (PriorityChannelImpl<?>) o;
        return priority == another.priority && super.queue.isEmpty() == another.queue.isEmpty();
    }

    /**
     * @apiNote
     * See notes to the {@link #equals(Object)} method
     */
    @Override
    public int hashCode() {
        return Objects.hash(priority, super.queue.isEmpty());
    }
}
