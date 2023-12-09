package net.zelcon.ibkr_api_client_wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.System.Logger;

import com.ib.client.*;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import net.zelcon.ibkr_api_client_wrapper.errors.TWSConnectionTimeoutException;
import net.zelcon.ibkr_api_client_wrapper.errors.TWSException;
import net.zelcon.ibkr_api_client_wrapper.response_types.*;

public class IB implements AutoCloseable {
    private static final Logger logger = System.getLogger(IB.class.getName());
    private final RequestBus requestBus;
    private final EReaderThread eReader;
    private final EClientSocket client;
    private final ExecutorService exec;
    private final RequestIdGenerator requestIdGenerator;

    public static IB connect(final String remoteHost, final int remotePort, final int clientId)
            throws TWSConnectionTimeoutException {
        ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
        IBEmitterRepository emitters = new IBEmitterRepository();
        RequestIdGenerator requestIdGenerator = new RequestIdGenerator();
        EReaderSignal signal = new EJavaSignal();
        EWrapper wrapper = new EWrapperImpl(requestIdGenerator, emitters);
        EClientSocket client = new EClientSocket(wrapper, signal);
        RequestBus requestBus = new RequestBus(emitters, client);
        client.eConnect(remoteHost, remotePort, clientId);
        // Note: `EClientSocket` must be connected before `EReaderThread` is to
        // be started. This is TWS API's rules, not mine.
        EReaderThread eReader = EReaderThread.start(client, signal);
        try {
            final var reqId = requestIdGenerator.getNextRequestId();
            logger.log(Logger.Level.INFO, "Connected to TWS. Next valid request ID: " + reqId);
            return new IB(exec, requestBus, eReader, client, requestIdGenerator);
        } catch (TWSConnectionTimeoutException e) {
            logger.log(Logger.Level.ERROR,
                    "Timed out trying to connect to TWS. Check your `remoteHost` and `remotePort`.");
            throw e;
        }
    }

    private IB(ExecutorService exec, RequestBus requestBus, EReaderThread eReader, EClientSocket client,
            RequestIdGenerator requestIdGenerator) {
        this.requestIdGenerator = requestIdGenerator;
        this.exec = exec;
        this.requestBus = requestBus;
        this.eReader = eReader;
        this.client = client;
    }

    @Override
    public void close() {
        eReader.close();
        client.eDisconnect();
        requestBus.close();
        exec.shutdown();
    }

    public CompletableFuture<Long> reqCurrentTime() {
        client.reqCurrentTime();
        return requestBus.getCurrentTimeObservable().map(CurrentTimeResponse::getCurrentTime).firstOrErrorStage()
                .toCompletableFuture();
    }

    public Future<NewsProvider[]> reqNewsProviders() {
        client.reqNewsProviders();
        return requestBus.getNewsProvidersObservable().map(NewsProvidersResponse::getNewsProviders).firstOrErrorStage()
                .toCompletableFuture();
    }

    public CompletableFuture<String> reqManagedAccounts() {
        client.reqManagedAccts();
        return requestBus.getManagedAccountsObservable().map(ManagedAccountsResponse::getManagedAccounts)
                .firstOrErrorStage().toCompletableFuture();
    }

    public CompletableFuture<FamilyCode[]> reqFamilyCodes() {
        client.reqFamilyCodes();
        return requestBus.getFamilyCodesObservable().map(FamilyCodesResponse::getFamilyCodes).firstOrErrorStage()
                .toCompletableFuture();
    }

    public CompletableFuture<List<HistoricalBar>> historicalBarsList(@NonNull Contract contract, String endDateTime,
            String durationStr, String barSizeSetting, String whatToShow, int useRTH, int formatDate)
            throws TWSConnectionTimeoutException {
        final int reqId = requestIdGenerator.getNextRequestId();
        final var flowable = this.requestBus.registerHistoricalBarEmitter(reqId);
        this.client.reqHistoricalData(reqId, contract, endDateTime, durationStr, barSizeSetting, whatToShow, useRTH,
                formatDate, false, null);
        ArrayList<HistoricalBar> lst = new ArrayList<>();
        CompletableFuture<List<HistoricalBar>> future = new CompletableFuture<>();
        flowable.subscribe(
                (bar) -> {
                    lst.add(bar);
                },
                (error) -> {
                    future.completeExceptionally(error);
                },
                () -> {
                    future.complete(lst);
                });
        return future;
    }

    public Flowable<HistoricalBar> historicalBars(@NonNull Contract contract, String endDateTime, String durationStr,
            String barSizeSetting, String whatToShow, int useRTH, int formatDate) throws TWSConnectionTimeoutException {
        final int reqId = requestIdGenerator.getNextRequestId();
        final var flowable = this.requestBus.registerHistoricalBarEmitter(reqId);
        this.client.reqHistoricalData(reqId, contract, endDateTime, durationStr, barSizeSetting, whatToShow, useRTH,
                formatDate, false, null);
        return flowable;
    }

    public Flowable<ContractDetailsResponse> contractDetails(@NonNull Contract contract) throws TWSConnectionTimeoutException {
        final int reqId = requestIdGenerator.getNextRequestId();
        final var flowable = this.requestBus.registerContractDetailsEmitter(reqId);
        this.client.reqContractDetails(reqId, contract);
        return flowable;
    }

    public CompletableFuture<List<ContractDetailsResponse>> contractDetailsList(@NonNull Contract contract) throws TWSConnectionTimeoutException {
        return contractDetails(contract).toList().toCompletionStage().toCompletableFuture();
    }
}
