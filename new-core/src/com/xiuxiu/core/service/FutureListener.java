package com.xiuxiu.core.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class FutureListener extends CompletableFuture<Boolean> implements ServiceListener {
    private ServiceListener listener;
    private AtomicBoolean started;

    public FutureListener(AtomicBoolean started) {
        this(started, null);
    }

    public FutureListener(AtomicBoolean started, ServiceListener listener) {
        this.listener = listener;
        this.started = started;
    }

    @Override
    public void onSucc(Object... args) {
        if (this.isDone()) {
            return;
        }
        this.complete(this.started.get());
        if (null != this.listener) {
            this.listener.onSucc(args);
        }
    }

    @Override
    public void onFail(Throwable err) {
        if (this.isDone()) {
            return;
        }
        this.completeExceptionally(err);
        if (null != this.listener) {
            this.listener.onFail(err);
        }
        throw err instanceof ServiceException ? (ServiceException) err : new ServiceException(err);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    public void monitor(BaseService service) {
        if (this.isDone()) {
            return;
        }
        runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    get(service.timeoutMillis(), TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    onFail(new ServiceException(String.format("service %s monitor timeout", service.getClass().getSimpleName())));
                }
            }
        });
    }
}
