package net.zelcon.ibkr_api_client_wrapper;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import net.zelcon.ibkr_api_client_wrapper.errors.TWSWaitForResponseTimeout;

public class IBReqSingleSubscriber<T> implements Flow.Subscriber<T>, AutoCloseable {
    private final System.Logger logger = System.getLogger(IBReqSingleSubscriber.class.getName());
    private ScheduledExecutorService scheduledExecutor;
    private final AtomicReference<T> item = new AtomicReference<>(null);
    private Flow.Subscription subscription;
    private static final Duration POLL_INTERVAL = Duration.ofMillis(200);
    private static final Duration POLL_TIMEOUT = Duration.ofSeconds(30);
    private final AtomicInteger timeoutCounter = new AtomicInteger(0);

    public CompletableFuture<T> get() {
        CompletableFuture<T> future = new CompletableFuture<>();
        if (item.get() != null) {
            future.complete(item.get());
            return future;
        }
        // busy wait until item is received
        if (scheduledExecutor == null)
            scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        final var pollTask = new Runnable() {
            @Override
            public void run() {
                if (item.get() != null) {
                    future.complete(item.get());
                    scheduledExecutor.shutdown();
                    return;
                }
                final var c = timeoutCounter.getAndAdd(1);
                if (c > POLL_TIMEOUT.toMillis() / POLL_INTERVAL.toMillis()) {
                    future.completeExceptionally(new TWSWaitForResponseTimeout());
                    scheduledExecutor.shutdown();
                    return;
                }
                scheduledExecutor.schedule(this, POLL_INTERVAL.toMillis(), TimeUnit.MILLISECONDS);
            }
        };
        scheduledExecutor.schedule(pollTask, POLL_INTERVAL.toMillis(), TimeUnit.MILLISECONDS);
        return future;
    }

    @Override
    public void close() {
        if (scheduledExecutor != null && !scheduledExecutor.isShutdown() && !scheduledExecutor.isTerminated())
            scheduledExecutor.shutdown();
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(T item) {
        this.item.set(item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        if (throwable instanceof InterruptedException) {
            logger.log(System.Logger.Level.WARNING, "IBReqOneShotSubscriber was interrupted");
        } else {
            logger.log(System.Logger.Level.ERROR, throwable);
            throwable.printStackTrace();
        }
    }

    @Override
    public void onComplete() {
        logger.log(System.Logger.Level.TRACE, "IBReqOneShotSubscriber completed");
    }

}
