package com.xiuxiu.app.protocol.login;

public class PLAccountAuthRespInfo {
    public int ret;             // 错误码, 0: 成功 其他失败
    public String msg;          // 错误信息
    public String gateway;      // 网关host
    public int port;            // 网关端口
    public long uid;            // 账号uid
    public String token;        // token
    public boolean realNameAuth;    // 是否实名认证
    public int status;          // 是否为新账号且为绑定推荐人 1：未绑定，0：不是新账号已绑定，2：账号不存在（数据迁移用）

    @Override
    public String toString() {
        return "PLAccountAuthRespInfo{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                ", gateway='" + gateway + '\'' +
                ", port=" + port +
                ", uid=" + uid +
                ", token='" + token + '\'' +
                ", realNameAuth=" + realNameAuth +
                ", status=" + status +
                '}';
    }
}
