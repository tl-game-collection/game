package com.xiuxiu.app.protocol.login;

public class PLGetAccountRespInfo {
    public int ret;             // 错误码, 0: 成功 其他失败
    public String msg;          // 错误信息
    public long userUid;        // 用戶ID
    public String phone;        // 手机号

    @Override
    public String toString() {
        return "PLGetAccountRespInfo{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                ", userUid=" + userUid +
                ", phone='" + phone + '\'' +
                '}';
    }
}
