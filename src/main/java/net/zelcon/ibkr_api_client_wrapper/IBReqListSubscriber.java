package net.zelcon.ibkr_api_client_wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import net.zelcon.ibkr_api_client_wrapper.errors.CourtesyTimeoutException;
import net.zelcon.ibkr_api_client_wrapper.response_types.IBResponse;

public class IBReqListSubscriber<T extends IBResponse> implements Flow.Subscriber<IBResponse> {
    private final CompletableFuture<List<T>> status = new CompletableFuture<>();
    private Flow.Subscription subscription;
    private final ScheduledExecutorService scheduledExec = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> courtesyTimeoutFuture;
    private final List<T> list = new ArrayList<>();

    protected Flow.Subscription getSubscription() {
        return subscription;
    }

    public CompletableFuture<List<T>> getFuture() {
        return status;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        this.status.whenComplete((v, e) -> {
            if (e != null) {
                this.subscription.cancel();
            }
        });
        if (!this.status.isDone())
            this.subscription.request(Long.MAX_VALUE);
        this.courtesyTimeoutFuture = scheduledExec.schedule(() -> {
            this.subscription.cancel();
            this.status.completeExceptionally(new CourtesyTimeoutException());
        }, 2, java.util.concurrent.TimeUnit.MINUTES);
    }

    @Override
    public void onError(Throwable throwable) {
        courtesyTimeoutFuture.cancel(false);
        this.status.completeExceptionally(throwable);
    }

    @Override
    public void onComplete() {
        courtesyTimeoutFuture.cancel(false);
        this.status.complete(list);
    }

    @Override
    public void onNext(IBResponse item) {
        list.add(cast(item));
    }

    @SuppressWarnings("unchecked")
    private <U extends IBResponse> U cast(IBResponse item) {
        return (U) item;
    }
}
