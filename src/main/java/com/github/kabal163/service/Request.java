package com.github.kabal163.service;

import lombok.Builder;
import lombok.Data;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.UUID;

@Data
@Builder
@Immutable
@ThreadSafe
public class Request {

    private final int priority;
    private final UUID payload;

    public static RequestBuilder builder() {
        return new RequestBuilder() {
            @Override
            public Request build() {
                if (super.priority < 1) throw new IllegalArgumentException("priority must not be less then 1");
                if (super.payload == null) throw new IllegalArgumentException("payload must not be null!");
                return super.build();
            }
        };
    }
}
