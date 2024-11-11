package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfGetWechatAssistant {
    public String wechat;               // 微信客服号
    public boolean isOfficialWechat;    // 是否是官方微信

    @Override
    public String toString() {
        return "PCLIPlayerNtfGetWechatAssistant{" +
                "wechat='" + wechat + '\'' +
                "，isOfficialWechat='" + isOfficialWechat +
                '}';
    }
}
