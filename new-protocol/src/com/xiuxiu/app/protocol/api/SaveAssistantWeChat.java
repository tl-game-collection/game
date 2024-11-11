package com.xiuxiu.app.protocol.api;

public class SaveAssistantWeChat {
    public String province;                 // 省
    public String city;                     // 市
    public String district;                 // 区
    public long adCode;                     // 区编号
    public String weChat;                   // 微信号

    @Override
    public String toString() {
        return "SaveAssistantWeChat{" +
                "province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", adCode=" + adCode +
                ", weChat='" + weChat + '\'' +
                '}';
    }
}
