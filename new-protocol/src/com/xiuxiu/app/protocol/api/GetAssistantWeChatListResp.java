package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetAssistantWeChatListResp extends ErrorMsg {
    public List<GetAssistantWeChatListResp.WechatInfo> data = new ArrayList<>();
    public static class WechatInfo {
        public String weChat;
        public String location;
        public String adCode;

        @Override
        public String toString() {
            return "WechatInfo{" +
                    "weChat='" + weChat + '\'' +
                    ", location='" + location + '\'' +
                    ", adCode='" + adCode + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GetAssistantWeChatListResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
