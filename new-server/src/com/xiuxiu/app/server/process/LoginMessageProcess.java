package com.xiuxiu.app.server.process;

import com.xiuxiu.core.net.Process;
import com.xiuxiu.core.net.Task;
import com.xiuxiu.core.thread.NameThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LoginMessageProcess implements Process {
    private final ExecutorService loginExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 2, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10000), new NameThreadFactory("LoginExecutorService"));

    @Override
    public void exec(Task task) {
        this.loginExecutor.submit(task);
    }

    @Override
    public void shutdown() {
        this.loginExecutor.shutdownNow();
    }
}
