package com.xiuxiu.core.net;

import com.xiuxiu.core.log.Logs;
import com.xiuxiu.core.net.codec.Codecs;
import com.xiuxiu.core.net.message.ResponseWrapper;
import io.netty.util.Timeout;

import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultInvokeFuture implements InvokeFuture {
    private final AtomicBoolean exec = new AtomicBoolean(false);
    private int invokeId;
    private Timeout timeout;
    private InvokeCallback callback;
    private ResponseWrapper response;

    public DefaultInvokeFuture(int invokeId, InvokeCallback callback) {
        this.invokeId = invokeId;
        this.callback = callback;
    }

    @Override
    public int invokeId() {
        return this.invokeId;
    }

    @Override
    public void addTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    @Override
    public void cancelTimeout() {
        if (null != this.timeout) {
            this.timeout.cancel();
        }
    }

    @Override
    public void putResponse(ResponseWrapper command) {
        this.response = command;
    }

    @Override
    public void exec() {
        if (null == callback) {
            return;
        }
        if (this.exec.compareAndSet(false, true)) {
            try {
                this.response.setBody(Codecs.getDecoder(this.response.getCodecType()).decoder(this.response.getCommandId(), (byte[]) this.response.getBody()));
                this.callback.onResponse(this.response);
            } catch (Exception e) {
                Logs.NET.error("Decoder error", e);
            }
        }
    }

    @Override
    public void timeout() {
        if (null == callback) {
            return;
        }
        if (this.exec.compareAndSet(false, true)) {
            this.callback.onTimeout();
        }
    }

    @Override
    public void sendFail() {
        if (null == callback) {
            return;
        }
        if (this.exec.compareAndSet(false, true)) {
            this.callback.onSendFail();
        }
    }

    @Override
    public void exception() {
        if (null == callback) {
            return;
        }
        if (this.exec.compareAndSet(false, true)) {
            try {
                this.response.setBody(Codecs.getDecoder(this.response.getCodecType()).decoder(this.response.getCommandId(), (byte[]) this.response.getBody()));
                this.callback.onException(this.response);
            } catch (Exception e) {
                Logs.NET.error("Decoder error", e);
            }
        }
    }

    @Override
    public InvokeCallback getInvokeCallback() {
        return this.callback;
    }
}
