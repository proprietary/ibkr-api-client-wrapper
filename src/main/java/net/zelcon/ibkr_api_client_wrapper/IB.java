package net.zelcon.ibkr_api_client_wrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.System.Logger;

import com.ib.client.*;

import net.zelcon.ibkr_api_client_wrapper.errors.TWSConnectionTimeoutException;
import net.zelcon.ibkr_api_client_wrapper.errors.TWSException;
import net.zelcon.ibkr_api_client_wrapper.response_types.ContractDetailsResponse;
import net.zelcon.ibkr_api_client_wrapper.response_types.HistoricalBar;

public class IB implements AutoCloseable {
    private static final Logger logger = System.getLogger(IB.class.getName());
    private final RequestBus requestBus;
    private final EReaderThread eReader;
    private final EClientSocket client;
    private final ExecutorService exec;

    public static IB connect(final String remoteHost, final int remotePort, final int clientId) throws TWSConnectionTimeoutException {
        ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
        RequestBus requestBus = new RequestBus(exec);
        EReaderSignal signal = new EJavaSignal();
        EWrapper wrapper = new EWrapperImpl(requestBus);
        EClientSocket client = new EClientSocket(wrapper, signal);
        client.eConnect(remoteHost, remotePort, clientId);
        // Note: `EClientSocket` must be connected before `EReaderThread` is to
        // be started. This is TWS API's rules, not mine.
        EReaderThread eReader = EReaderThread.start(client, signal);
        try {
            final var reqId = RequestIdGenerator.getInstance().getNextRequestId();
            logger.log(Logger.Level.INFO, "Connected to TWS. Next valid request ID: " + reqId);
            return new IB(exec, requestBus, eReader, client);
        } catch (TWSConnectionTimeoutException e) {
            logger.log(Logger.Level.ERROR,
                    "Timed out trying to connect to TWS. Check your `remoteHost` and `remotePort`.");
            throw e;
        }
    }

    private IB(ExecutorService exec, RequestBus requestBus, EReaderThread eReader, EClientSocket client) {
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

    public CompletableFuture<List<HistoricalBar>> reqHistoricalData(@NonNull Contract contract, String endDateTime,
            String durationStr, String barSizeSetting, String whatToShow, int useRTH, int formatDate)
            throws TWSException, TWSConnectionTimeoutException {
        final var reqId = RequestIdGenerator.getInstance().getNextRequestId();
        requestBus.register(reqId);
        client.reqHistoricalData(reqId, contract, endDateTime, durationStr, barSizeSetting, whatToShow, useRTH,
                formatDate, false, null);
        final var subscriber = new IBReqListSubscriber<HistoricalBar>();
        requestBus.getPublisher(reqId).orElseThrow().subscribe(subscriber);
        return subscriber.getFuture();
    }

    public CompletableFuture<List<ContractDetailsResponse>> reqContractDetails(@NonNull Contract contract)
            throws TWSException, TWSConnectionTimeoutException {
        final var reqId = RequestIdGenerator.getInstance().getNextRequestId();
        requestBus.register(reqId);
        client.reqContractDetails(reqId, contract);
        final var subscriber = new IBReqListSubscriber<ContractDetailsResponse>();
        requestBus.getPublisher(reqId).orElseThrow().subscribe(subscriber);
        return subscriber.getFuture();
    }

    public CompletableFuture<Long> reqCurrentTime() {
        client.reqCurrentTime();
        return requestBus.getCurrentTime();
    }

    public CompletableFuture<NewsProvider[]> reqNewsProviders() {
        client.reqNewsProviders();
        return requestBus.getNewsProviders();
    }
}
