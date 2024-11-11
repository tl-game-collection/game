package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class WeChatInfoResp extends ErrorMsg {
    
    public long playerId; 
    public String name;
    public Integer count;

    @Override
    public String toString() {
        return "WeChatInfoResp{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                ", playerId='" + playerId + '\'' +
                ", name='" + name + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
}
