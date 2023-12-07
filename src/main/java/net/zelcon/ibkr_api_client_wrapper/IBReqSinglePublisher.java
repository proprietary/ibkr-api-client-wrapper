package net.zelcon.ibkr_api_client_wrapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class IBReqSinglePublisher<T> implements Flow.Publisher<T>, AutoCloseable {
    private T item;
    private final Object signal = new Object();
    private final ExecutorService executor;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    public IBReqSinglePublisher(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new Flow.Subscription() {
            private Future<T> future;

            @Override
            public void request(long n) {
                if (n != 1) {
                    subscriber.onError(new IllegalArgumentException("Only one item at a time may be requested"));
                }
                future = executor.submit(() -> {
                    synchronized (signal) {
                        while (!Thread.currentThread().isInterrupted() && !isClosed.get()) {
                            try {
                                signal.wait();
                                if (isClosed.get())
                                    return item;
                                if (item == null)
                                    continue;
                                subscriber.onNext(item);
                            } catch (InterruptedException e) {
                                return item;
                            }
                        }
                        return item;
                    }
                });
            }

            @Override
            public void cancel() {
                if (future != null)
                    future.cancel(true);
            }
        });
    }

    public void submit(T item) {
        synchronized (signal) {
            this.item = item;
            signal.notifyAll();
        }
    }

    @Override
    public void close() {
        synchronized (signal) {
            isClosed.set(true);
            signal.notifyAll();
        }
    }
}
