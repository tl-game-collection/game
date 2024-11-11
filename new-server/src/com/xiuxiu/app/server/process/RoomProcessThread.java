package com.xiuxiu.app.server.process;

import com.xiuxiu.core.net.Task;
import com.xiuxiu.core.thread.ConsumeThread;

public class RoomProcessThread extends ConsumeThread<Task> {
    public RoomProcessThread(int index, int max) {
        super("RoomProcessThread-" + index + "/" + max);
    }

    @Override
    protected void exec(Task value) {
        value.run();
    }
}
