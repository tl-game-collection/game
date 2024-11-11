package com.xiuxiu.app.protocol.login;

public class PLGetAccountInfo {
    public long userUid;        // 用戶ID

    @Override
    public String toString() {
        return "PLGetAccountInfo{" +
                "userUid=" +  userUid+
                '}';
    }
}
