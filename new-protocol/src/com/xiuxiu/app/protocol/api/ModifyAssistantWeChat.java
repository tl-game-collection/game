package com.xiuxiu.app.protocol.api;

public class ModifyAssistantWeChat {
    public long adCode;                     // 区编号
    public String weChat;                   // 微信号

    @Override
    public String toString() {
        return "ModifyAssistantWeChat{" +
                "adCode=" + adCode +
                ", weChat='" + weChat + '\'' +
                '}';
    }
}
