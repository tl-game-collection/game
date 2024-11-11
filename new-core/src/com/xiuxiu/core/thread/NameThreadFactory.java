package com.xiuxiu.core.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NameThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private ThreadGroup group;
    private String nameFreFix;
    private boolean isDaemon;

    public NameThreadFactory() {
        this("boxcarThreadPool");
    }

    public NameThreadFactory(String name) {
        this(name, false);
    }

    public NameThreadFactory(String preFix, boolean daemon) {
        SecurityManager securityManager = System.getSecurityManager();
        this.group = null == securityManager ? Thread.currentThread().getThreadGroup() : securityManager.getThreadGroup();
        this.nameFreFix = preFix + "-" + poolNumber.getAndIncrement() + "-thread-";
        this.isDaemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(this.group, r, this.nameFreFix + this.threadNumber.getAndIncrement(), 0);
        thread.setDaemon(this.isDaemon);
        return thread;
    }
}
