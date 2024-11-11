package com.xiuxiu.core.boot;

public abstract class BootJob {
    protected BootJob next;

    protected abstract void start();

    protected abstract void stop();

    public void startNext() {
        if (null != this.next) {
            // log
            this.next.start();
        }
    }

    public void stopNext() {
        if (null != this.next) {
            this.next.stop();
            // log
        }
    }

    public BootJob next(BootJob next) {
        this.next = next;
        return this.next;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public String getNextName() {
        if (null != this.next) {
            return this.next.getName();
        }
        return "";
    }
}
