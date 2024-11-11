package com.xiuxiu.app.protocol.login;

public class PLCheckAccountRespInfo {
    public int ret;         // 错误码: 0: 成功, 其他错误
    public String msg;      // 错误信息

    @Override
    public String toString() {
        return "PLCheckAccountRespInfo{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
