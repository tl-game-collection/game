package com.xiuxiu.core.net;

import com.xiuxiu.core.net.message.ResponseWrapper;
import io.netty.util.Timeout;

public interface InvokeFuture {
    int invokeId();

    void addTimeout(Timeout timeout);

    void cancelTimeout();

    void putResponse(ResponseWrapper command);

    void exec();

    void timeout();

    void sendFail();

    void exception();

    InvokeCallback getInvokeCallback();
}
