package com.xiuxiu.app.server.process;

import com.xiuxiu.core.net.Task;
import com.xiuxiu.core.thread.ConsumeThread;

public class PlayerProcessThread extends ConsumeThread<Task> {
    public PlayerProcessThread(int index, int max) {
        super("PlayerProcessThread-" + index + '/' + max);
    }

    @Override
    protected void exec(Task value) {
        value.run();
    }
}
