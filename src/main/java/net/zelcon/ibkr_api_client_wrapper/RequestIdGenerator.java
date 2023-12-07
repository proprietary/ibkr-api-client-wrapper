package net.zelcon.ibkr_api_client_wrapper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.zelcon.ibkr_api_client_wrapper.errors.TWSConnectionTimeoutException;

import java.time.Duration;

public class RequestIdGenerator {
    private static RequestIdGenerator instance;
    private static final int UNINITIALIZED = Integer.MIN_VALUE;
    private final AtomicInteger requestId = new AtomicInteger(UNINITIALIZED);
    private final CountDownLatch latch = new CountDownLatch(1);
    private static final Duration WAIT_FOR_CONNECTION_TIMEOUT = Duration.ofSeconds(15);

    private RequestIdGenerator() {
        // constructor is private to prevent initialization outside of getInstance()
    }

    public static synchronized RequestIdGenerator getInstance() {
        if (instance == null) {
            instance = new RequestIdGenerator();
        }
        return instance;
    }

    public int getNextRequestId() throws TWSConnectionTimeoutException {
        final var prev = requestId.get();
        if (prev == UNINITIALIZED) {
            try {
                boolean ok = latch.await(WAIT_FOR_CONNECTION_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
                if (!ok) {
                    throw new TWSConnectionTimeoutException();
                }
            } catch (InterruptedException e) {
                throw new TWSConnectionTimeoutException();
            }
            return requestId.get();
        }
        return requestId.getAndIncrement();
    }


    protected void reset(int requestId) {
        this.requestId.set(requestId);
        latch.countDown();
    }
}
