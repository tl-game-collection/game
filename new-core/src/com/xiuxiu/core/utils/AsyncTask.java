package com.xiuxiu.core.utils;

import com.xiuxiu.core.thread.NameThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncTask {
    public static final AsyncTask I = AsyncTaskHolder.instance;
    private final ExecutorService executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 2, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10000), new NameThreadFactory("AsyncTask"));

    private AsyncTask() {
    }

    public void addTask(Runnable runnable) {
        this.executorService.submit(runnable);
    }

    public void shutdown() {
        this.executorService.shutdownNow();
    }

    private static class AsyncTaskHolder {
        private static AsyncTask instance = new AsyncTask();
    }
}
