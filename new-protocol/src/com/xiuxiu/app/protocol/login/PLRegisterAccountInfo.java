package com.xiuxiu.app.protocol.login;

public class PLRegisterAccountInfo {
    public int type;            // 1: 手机注册 2; 快速注册
    public String phone;        // 手机号
    public String authCode;     // 验证码
    public String passwd;       // 密码md5(passwd)
    public String sign;         // 签名md5(phone + passwd + authCode + key)
    public String mac;          // 手机mac
    public String phoneVer;     // 手机版本
    public String phoneOsVer;   // 手机操作系统版本
    public long topUid;       // 上级用户ID {"gid":111,"uid":222}
    public long topGid;       // 上级群ID
    public int bizChannel = -1; // 业务渠道

    @Override
    public String toString() {
        return "PLRegisterAccountInfo{" +
                "type=" + type +
                ", phone='" + phone + '\'' +
                ", authCode='" + authCode + '\'' +
                ", passwd='" + passwd + '\'' +
                ", sign='" + sign + '\'' +
                ", mac='" + mac + '\'' +
                ", phoneVer='" + phoneVer + '\'' +
                ", phoneOsVer='" + phoneOsVer + '\'' +
                ", topUid=" + topUid +
                ", topGid=" + topGid +
                ", bizChannel=" + bizChannel +
                '}';
    }
}
