package com.xiuxiu.core.service;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseService implements Service {
    private final AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void init() {

    }

    @Override
    public void start() {
        FutureListener futureListener = new FutureListener(this.started);
        this.start(futureListener);
        futureListener.join();
    }

    @Override
    public void stop() {
        FutureListener futureListener = new FutureListener(this.started);
        this.stop(futureListener);
        futureListener.join();
    }

    @Override
    public void start(ServiceListener listener) {
        this.tryStart(listener, new FunctionEx() {
            @Override
            public void apply(FutureListener serviceListener) throws Exception {
                doStart(serviceListener);
            }
        });
    }

    protected void doStart(FutureListener serviceListener) {
        serviceListener.onSucc();
    }

    protected void doStop(FutureListener serviceListener) {
        serviceListener.onSucc();
    }

    @Override
    public void stop(ServiceListener serviceListener) {
        this.tryStop(serviceListener, new FunctionEx() {
            @Override
            public void apply(FutureListener serviceListener) throws Exception {
                doStop(serviceListener);
            }
        });
    }

    private void tryStart(ServiceListener serviceListener, FunctionEx func) {
        FutureListener futureListener = this.wrap(serviceListener);
        if (this.started.compareAndSet(false, true)) {
            try {
                this.init();
                func.apply(futureListener);
                futureListener.monitor(this);
            } catch (Exception e) {
                futureListener.onFail(e);
                throw new ServiceException(e);
            }
        } else {
            if (this.throwIfStart()) {
                futureListener.onFail(new ServiceException(String.format("service %s already start", this.getClass().getSimpleName())));
            } else {
                futureListener.onSucc();
            }
        }
    }

    private void tryStop(ServiceListener serviceListener, FunctionEx func) {
        FutureListener futureListener = this.wrap(serviceListener);
        if (this.started.compareAndSet(true, false)) {
            try {
                func.apply(futureListener);
                futureListener.monitor(this);
            } catch (Exception e) {
                futureListener.onFail(e);
                throw new ServiceException(e);
            }
        } else {
            if (this.throwIfStop()) {
                futureListener.onFail(new ServiceException(String.format("service %s already stop", this.getClass().getSimpleName())));
            } else {
                futureListener.onSucc();
            }
        }
    }

    @Override
    public boolean running() {
        return this.started.get();
    }

    protected long timeoutMillis() {
        return 10 * 1000;
    }

    protected boolean throwIfStart() {
        return true;
    }

    protected boolean throwIfStop() {
        return true;
    }

    public FutureListener wrap(ServiceListener serviceListener) {
        if (serviceListener instanceof FutureListener) {
            return (FutureListener) serviceListener;
        }
        FutureListener futureListener = new FutureListener(this.started, serviceListener);
        return futureListener;
    }

    public interface FunctionEx {
        void apply(FutureListener serviceListener) throws Exception;
    }
}
