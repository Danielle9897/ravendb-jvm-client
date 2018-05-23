package net.ravendb.client.documents.changes;

import net.ravendb.client.primitives.CleanCloseable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;

public class ChangesObservable<T, TConnectionState extends IChangesConnectionState> implements IChangesObservable<T> {

    private final TConnectionState _connectionState;
    private final Function<T, Boolean> _filter;
    private final ConcurrentHashMap<IObserver<T>, Boolean> _subscribers = new ConcurrentHashMap<>();


    ChangesObservable(TConnectionState connectionState, Function<T, Boolean> filter) {
        _connectionState = connectionState;
        _filter = filter;
    }

    public CleanCloseable subscribe(IObserver<T> observer) {
        _connectionState.inc();
        _subscribers.put(observer, true);

        return () -> {
            _connectionState.dec();
            _subscribers.remove(observer);
        };
    }

    public void send(T msg) {
        try {
            if (!_filter.apply(msg)) {
                return;
            }
        } catch (Exception e) {
            error(e);
            return;
        }

        for (IObserver<T> subscriber : _subscribers.keySet()) {
            subscriber.onNext(msg);
        }
    }

    public void error(Exception e) {
        for (IObserver<T> subscriber : _subscribers.keySet()) {
            subscriber.onError(e);
        }
    }
}