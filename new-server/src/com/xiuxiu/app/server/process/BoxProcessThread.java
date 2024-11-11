package com.xiuxiu.app.server.process;

import com.xiuxiu.core.net.Task;
import com.xiuxiu.core.thread.ConsumeThread;

public class BoxProcessThread extends ConsumeThread<Task> {
    public BoxProcessThread(int index, int max) {
        super("BoxProcessThread-" + index + '/' + max);
    }

    @Override
    protected void exec(Task value) {
        value.run();
    }
}
