package com.xiuxiu.core.net;

import com.xiuxiu.core.log.Logs;
import com.xiuxiu.core.net.message.ResponseWrapper;
import com.xiuxiu.core.net.protocol.ErrorCode;
import com.xiuxiu.core.net.protocol.ErrorMsg;

public interface InvokeCallback {
    void onResponse(ResponseWrapper message);

    default void onTimeout() {
        Logs.NET.warn("Request timeout");
        this.onError(new ErrorMsg(ErrorCode.NET_TIMEOUT));
    }

    default void onSendFail() {
        Logs.NET.warn("Request send fail");
        this.onError(new ErrorMsg(ErrorCode.NET_ERROR));
    }

    default void onException(ResponseWrapper error) {
        Logs.NET.error("Request remote execute exception err:%s", error.getBody());
        Object err = error.getBody();
        if (null != err && err instanceof ErrorMsg) {
            this.onError(((ErrorMsg) err));
        } else {
            this.onError(new ErrorMsg(ErrorCode.SERVER_INTERNAL_ERROR));
        }
    }

    void onError(ErrorMsg err);
}
