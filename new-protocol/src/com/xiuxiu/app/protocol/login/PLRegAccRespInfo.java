package com.xiuxiu.app.protocol.login;

public class PLRegAccRespInfo {
    public int ret;         // 错误码: 0: 成功, 其他错误
    public String msg;      // 错误信息
    public long uid;        // 账号id

    @Override
    public String toString() {
        return "PLRegAccResp{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                ", uid=" + uid +
                '}';
    }
}
