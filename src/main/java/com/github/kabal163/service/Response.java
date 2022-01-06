package com.github.kabal163.service;

import lombok.Builder;
import lombok.Data;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

@Data
@Builder
@Immutable
@ThreadSafe
public class Response {

    private final Request request;
    private final String payload;

    public static ResponseBuilder builder() {
        return new ResponseBuilder() {
            @Override
            public Response build() {
                if (super.request == null) throw new IllegalArgumentException("request must not be null!");
                if (super.payload == null) throw new IllegalArgumentException("payload must not be null!");
                return super.build();
            }
        };
    }
}
