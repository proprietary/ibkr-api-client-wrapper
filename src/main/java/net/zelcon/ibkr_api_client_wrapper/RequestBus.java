package net.zelcon.ibkr_api_client_wrapper;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import com.ib.client.*;

public class RequestBus implements AutoCloseable {
    private final ExecutorService exec;
    private final ConcurrentHashMap<Integer, IBReqPublisher> publishers = new ConcurrentHashMap<>();
    private final IBReqSinglePublisher<NewsProvider[]> newsProvidersPublisher;
    private final IBReqSingleSubscriber<NewsProvider[]> newsProvidersSubscriber = new IBReqSingleSubscriber<>();
    private final IBReqSinglePublisher<FamilyCode[]> familyCodesPublisher;
    private final IBReqSingleSubscriber<FamilyCode[]> familyCodesSubscriber = new IBReqSingleSubscriber<>();
    private final IBReqSinglePublisher<Long> currentTimePublisher;
    private final IBReqSingleSubscriber<Long> currentTimeSubscriber = new IBReqSingleSubscriber<>();
    private final IBReqSinglePublisher<String> managedAccountsPublisher;
    private final IBReqSingleSubscriber<String> managedAccountsSubscriber = new IBReqSingleSubscriber<>();

    public RequestBus(ExecutorService exec) {
        this.exec = exec;
        this.newsProvidersPublisher = new IBReqSinglePublisher<>(exec);
        this.newsProvidersPublisher.subscribe(this.newsProvidersSubscriber);
        this.familyCodesPublisher = new IBReqSinglePublisher<>(exec);
        this.familyCodesPublisher.subscribe(this.familyCodesSubscriber);
        this.currentTimePublisher = new IBReqSinglePublisher<>(exec);
        this.currentTimePublisher.subscribe(this.currentTimeSubscriber);
        this.managedAccountsPublisher = new IBReqSinglePublisher<>(exec);
        this.managedAccountsPublisher.subscribe(this.managedAccountsSubscriber);
    }

    public Optional<IBReqPublisher> getPublisher(int requestId) {
        return Optional.ofNullable(publishers.get(requestId));
    }

    public void register(int requestId) {
        publishers.putIfAbsent(requestId, new IBReqPublisher(exec));
    }

    @Override
    public void close() {
        publishers.values().forEach(IBReqPublisher::close);
        newsProvidersPublisher.close();
        familyCodesPublisher.close();
        currentTimePublisher.close();
    }

    public CompletableFuture<NewsProvider[]> getNewsProviders() {
        return this.newsProvidersSubscriber.get();
    }

    public CompletableFuture<FamilyCode[]> getFamilyCodes() {
        return this.familyCodesSubscriber.get();
    }

    public CompletableFuture<Long> getCurrentTime() {
        return this.currentTimeSubscriber.get();
    }

    public CompletableFuture<String> getManagedAccounts() {
        return this.managedAccountsSubscriber.get();
    }

    public IBReqSinglePublisher<Long> getCurrentTimePublisher() {
        return this.currentTimePublisher;
    }

    public IBReqSinglePublisher<FamilyCode[]> getFamilyCodesPublisher() {
        return this.familyCodesPublisher;
    }

    public IBReqSinglePublisher<NewsProvider[]> getNewsProvidersPublisher() {
        return this.newsProvidersPublisher;
    }

    public IBReqSinglePublisher<String> getManagedAccountsPublisher() {
        return this.managedAccountsPublisher;
    }
}
