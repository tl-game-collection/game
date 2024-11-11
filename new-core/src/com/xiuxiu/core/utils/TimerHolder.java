package com.xiuxiu.core.utils;

import com.xiuxiu.core.thread.NameThreadFactory;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;

import java.util.concurrent.TimeUnit;

public class TimerHolder {
    private static class DefaultInstance {
        private static final Timer INSTANCE = new HashedWheelTimer(new NameThreadFactory("DefaultTimer-10"), 10, TimeUnit.MILLISECONDS);
    }

    private TimerHolder() {

    }

    public static Timer getTimer() {
        return DefaultInstance.INSTANCE;
    }

    public static void stop() {
        DefaultInstance.INSTANCE.stop();
    }

}
