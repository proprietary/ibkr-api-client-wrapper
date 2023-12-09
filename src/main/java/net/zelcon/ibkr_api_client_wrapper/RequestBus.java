package net.zelcon.ibkr_api_client_wrapper;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import net.zelcon.ibkr_api_client_wrapper.response_types.*;
import static com.google.common.base.Preconditions.*;
import com.ib.client.*;

public class RequestBus implements AutoCloseable {
    private final EClientSocket twsClient;
    private final IBEmitterRepository emitters;

    // these are special cases because they don't have a request ID associated
    // with the request/response cycle in the TWS API
    private final Observable<CurrentTimeResponse> currentTimeObservable;
    private final Observable<FamilyCodesResponse> familyCodesObservable;
    private final Observable<ManagedAccountsResponse> managedAccountsObservable;
    private final Observable<NewsProvidersResponse> newsProvidersObservable;

    protected IBEmitterRepository getEmitters() {
        return emitters;
    }

    public Flowable<HistoricalBar> registerHistoricalBarEmitter(int requestId) {
        checkArgument(requestId > 0, "Request ID must be greater than 0.");
        if (getEmitters().<HistoricalBar>getFlowableEmitter(requestId).isPresent()) {
            throw new IllegalStateException("Library attempted to create a Request ID that already exists (request ID="
                    + requestId + "). This is a serious error in the logic of the library.");
        }
        final var f = Flowable.<HistoricalBar>create(emitter -> {
            getEmitters().<HistoricalBar>addIbEmitter(requestId, emitter);
            emitter.setCancellable(() -> {
                getEmitters().removeIbEmitter(requestId);
                twsClient.cancelHistoricalData(requestId);
            });
        }, BackpressureStrategy.BUFFER);
        return f;
    }

    public Flowable<ContractDetailsResponse> registerContractDetailsEmitter(int requestId) {
        checkArgument(requestId > 0, "Request ID must be greater than 0.");
        checkState(getEmitters().<ContractDetailsResponse>getFlowableEmitter(requestId).isEmpty(),
                "Internal library logic error: Library attempted to create two requests with the same Request ID");
        final var f = Flowable.<ContractDetailsResponse>create(emitter -> {
            getEmitters().<ContractDetailsResponse>addIbEmitter(requestId, emitter);
            emitter.setCancellable(() -> {
                getEmitters().removeIbEmitter(requestId);
            });
        }, BackpressureStrategy.BUFFER);
        return f;
    }

    public RequestBus(IBEmitterRepository emitters, final EClientSocket twsClient) {
        this.emitters = emitters;
        this.twsClient = twsClient;
        this.currentTimeObservable = noReqIdObservable(CurrentTimeResponse.class);
        this.familyCodesObservable = noReqIdObservable(FamilyCodesResponse.class);
        this.managedAccountsObservable = noReqIdObservable(ManagedAccountsResponse.class);
        this.newsProvidersObservable = noReqIdObservable(NewsProvidersResponse.class);
    }

    private <T extends IBResponse> Observable<T> noReqIdObservable(final Class<T> clazz) {
        return Observable.<T>create(emitter -> {
            emitters.<T>addIbEmitter(clazz, emitter);
            emitter.setCancellable(() -> {
                emitters.removeIbEmitter(clazz);
            });
        });
    }

    public Observable<CurrentTimeResponse> getCurrentTimeObservable() {
        return currentTimeObservable;
    }

    public Observable<FamilyCodesResponse> getFamilyCodesObservable() {
        return familyCodesObservable;
    }

    public Observable<ManagedAccountsResponse> getManagedAccountsObservable() {
        return managedAccountsObservable;
    }

    public Observable<NewsProvidersResponse> getNewsProvidersObservable() {
        return newsProvidersObservable;
    }

    @Override
    public void close() {
        this.emitters.close();
    }
}
