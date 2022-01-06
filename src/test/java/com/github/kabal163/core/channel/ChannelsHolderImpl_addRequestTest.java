package com.github.kabal163.core.channel;

import com.github.kabal163.config.Config;
import com.github.kabal163.service.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * It's not exhaustive tests for the application,
 * but a small peace in order to represent myself
 */
class ChannelsHolderImpl_addRequestTest {

    static final int TEST_RESPONSE_CHANNEL_CAPACITY = 100;
    static final int TEST_MIN_PRIORITY = 1;
    static final int TEST_MAX_PRIORITY = 999;

    @Mock
    Config config;

    ChannelsHolderImpl underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(config.getResponseChannelCapacity()).thenReturn(TEST_RESPONSE_CHANNEL_CAPACITY);

        underTest = new ChannelsHolderImpl(config);
    }

    @Test
    @DisplayName("Given null Request " +
            "When call addRequest " +
            "Then throws IllegalArgumentException")
    void givenNullRequest_whenCallAddRequest_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> underTest.addRequest(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given 10 producers, each in separate thread " +
            "When call addRequest 1 million times for each producer " +
            "Then the total requests number in request channels must be 10 millions")
    void givenTenProducers_whenCallAddRequestMillionTimesEach_thenTotalNumberOfRequestsInRequestChannelsMustBeTenMillions() throws InterruptedException {
        final int numberOfProducers = 10;
        final int numberOfCalls = 1_000_000;
        final int expected = numberOfProducers * numberOfCalls;
        final Random random = new Random();

        runInParallel(
                numberOfProducers,
                numberOfCalls,
                i -> random.nextInt(TEST_MAX_PRIORITY) + TEST_MIN_PRIORITY
        ).await();

        int actual = 0;
        Iterator<PriorityChannel<Request>> iter = underTest.getRequestChannelsIterator();
        while (iter.hasNext()) {
            PriorityChannel<Request> channel = iter.next();
            Request r;
            while ((r = channel.pollItem()) != null) {
                actual++;
            }
        }

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 100, 999, 10_000, Integer.MAX_VALUE})
    @DisplayName("Given a single priority for all requests " +
            "When call addRequest multiple times " +
            "Then all the requests will be added to the same request channel")
    void givenSinglePriority_whenCallAddRequestMultipleTimes_thenAllRequestsWillBeAddedToTheSameRequestChannel(int priority) throws InterruptedException {
        final int expectedNumberOfChannels = 1;
        final int numberOfProducers = 10;
        final int numberOfCalls = 10_000;
        final int expectedNumberOfRequests = numberOfProducers * numberOfCalls;


        runInParallel(
                numberOfProducers,
                numberOfCalls,
                i -> priority
        ).await();

        int actualNumberOfChannels = 0;
        int actualNumberOfRequests = 0;
        Iterator<PriorityChannel<Request>> iter = underTest.getRequestChannelsIterator();
        while (iter.hasNext()) {
            PriorityChannel<Request> channel = iter.next();
            actualNumberOfChannels++;
            Request r;
            while ((r = channel.pollItem()) != null) {
                actualNumberOfRequests++;
            }
        }

        assertThat(actualNumberOfChannels).isEqualTo(expectedNumberOfChannels);
        assertThat(actualNumberOfRequests).isEqualTo(expectedNumberOfRequests);
    }

    @Test
    @DisplayName("Given some number of priorities " +
            "When call addRequest multiple times " +
            "Then a new channel must be created per priority")
    void givenSomeNumberOfPriorities_whenCallAddRequestMultipleTimes_thenNewChannelMustBeCreatedPerPriority() throws InterruptedException {
        final int expectedNumberOfChannels = 300_000;
        final int numberOfProducers = 10;

        runInParallel(
                numberOfProducers,
                expectedNumberOfChannels,
                i -> i + 1
        ).await();

        int actualNumberOfChannels = 0;
        Iterator<PriorityChannel<Request>> iter = underTest.getRequestChannelsIterator();
        while (iter.hasNext()) {
            iter.next();
            actualNumberOfChannels++;
        }

        assertThat(actualNumberOfChannels).isEqualTo(expectedNumberOfChannels);
    }

    private CountDownLatch runInParallel(int numberOfThreads,
                                         int numberOfCalls,
                                         Function<Integer, Integer> priorityProducer) {
        final ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);
        final CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);
        final CountDownLatch latch = new CountDownLatch(numberOfThreads);


        for (int i = 0; i < numberOfThreads; i++) {
            pool.execute(() -> {
                try {
                    barrier.await();
                    for (int j = 0; j < numberOfCalls; j++) {
                        underTest.addRequest(Request.builder()
                                .priority(priorityProducer.apply(j))
                                .payload(UUID.randomUUID())
                                .build());
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        return latch;
    }
}