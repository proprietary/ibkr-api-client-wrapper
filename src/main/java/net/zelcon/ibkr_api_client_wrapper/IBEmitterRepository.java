package net.zelcon.ibkr_api_client_wrapper;

import io.reactivex.rxjava3.core.Emitter;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.ObservableEmitter;
import net.zelcon.ibkr_api_client_wrapper.response_types.*;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class IBEmitterRepository implements AutoCloseable {
    private final ConcurrentHashMap<Class<? extends IBResponse>, ObservableEmitter<? extends IBResponse>> noReqIdEmitters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Emitter<? extends IBResponse>> ibEmitters = new ConcurrentHashMap<>();

    public <T extends IBResponse> void addIbEmitter(final int requestId, final FlowableEmitter<T> emitter) {
        ibEmitters.put(requestId, emitter);
    }

    public <T extends IBResponse> void addIbEmitter(Class<T> clazz, final ObservableEmitter<T> emitter) {
        noReqIdEmitters.put(clazz, emitter);
    }

    public <T extends IBResponse> void addIbEmitter(final int requestId, final ObservableEmitter<T> emitter) {
        ibEmitters.put(requestId, emitter);
    }

    @SuppressWarnings("unchecked")
    public <T extends IBResponse> Optional<FlowableEmitter<T>> getFlowableEmitter(final int requestId) {
        final var emitter = ibEmitters.get(requestId);
        if (emitter == null) {
            return Optional.empty();
        }
        try {
            FlowableEmitter<T> castedEmitter = (FlowableEmitter<T>) emitter;
            return Optional.of(castedEmitter);
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends IBResponse> Optional<ObservableEmitter<T>> getObservableEmitter(final int requestId) {
        final var emitter = ibEmitters.get(requestId);
        if (emitter == null) {
            return Optional.empty();
        }
        try {
            ObservableEmitter<T> castedEmitter = (ObservableEmitter<T>) emitter;
            return Optional.of(castedEmitter);
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends IBResponse> Optional<ObservableEmitter<T>> getObservableEmitter(Class<T> clazz) {
        final var emitter = noReqIdEmitters.get(clazz);
        if (emitter == null) {
            return Optional.empty();
        }
        try {
            ObservableEmitter<T> castedEmitter = (ObservableEmitter<T>) emitter;
            return Optional.of(castedEmitter);
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    public void removeIbEmitter(final int requestId) {
        ibEmitters.remove(requestId);
    }

    public <T extends IBResponse> void removeIbEmitter(Class<T> clazz) {
        noReqIdEmitters.remove(clazz);
    }

    @Override
    public void close() {
        ibEmitters.values().forEach(Emitter::onComplete);
        ibEmitters.clear();
        noReqIdEmitters.values().forEach(Emitter::onComplete);
        noReqIdEmitters.clear();
    }
}