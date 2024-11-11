package com.xiuxiu.core.net;

public interface Process {
    void exec(Task task);
    void shutdown();
}
