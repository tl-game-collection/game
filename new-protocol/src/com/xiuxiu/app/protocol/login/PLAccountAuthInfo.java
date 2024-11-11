package com.xiuxiu.app.protocol.login;

public class PLAccountAuthInfo {
    public int type;        // 1: 手机号登陆, 2: 快速登陆, 3: 微信登陆, 4: 游客登陆, 5: token登陆, 6: 尝试微信登陆（不创建新账号）7闲聊登陆（不存在就创建）8钉钉登陆（不存在就创建）
    public String phone;    // 手机号
    public String mac;      // 游客登陆凭证(只有游客登陆生效)
    public int channel;     // 渠道
    public String authCode; // 验证码
    public String passwd;   // 密码
    public String sign;     // 签名(只有手机号登陆才生效) md5(channel + phone + passwd + key)
    public String token;    // token
    public long topUid;     // 上级用户ID
    public long topGid;     // 上级所在群ID
    public int bizChannel = -1;  // 业务渠道

    @Override
    public String toString() {
        return "PLAccountAuthInfo{" +
                "type=" + type +
                ", phone='" + phone + '\'' +
                ", mac='" + mac + '\'' +
                ", channel=" + channel +
                ", bizChannel=" + bizChannel +
                ", authCode='" + authCode + '\'' +
                ", passwd='" + passwd + '\'' +
                ", sign='" + sign + '\'' +
                ", token='" + token + '\'' +
                ", topUid=" + topUid +
                ", topGid=" + topGid +
                '}';
    }
}
