package com.xiuxiu.core.net.protocol;

import com.alibaba.fastjson.annotation.JSONField;

public class ErrorMsg {
    public int ret;
    public String msg;

    public ErrorMsg() {
        this(ErrorCode.OK);
    }

    public ErrorMsg(IErrorCode err) {
        this.ret = err.getRet();
        this.msg = err.getMsg();
    }

    @JSONField(serialize = false, deserialize = false)
    public void setRet(IErrorCode err) {
        this.ret = err.getRet();
        this.msg = err.getMsg();
    }

    @Override
    public String toString() {
        return "ErrorMsg{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
