package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.HashMap;

public class GetWhiteListResp extends ErrorMsg {
    protected HashMap<Long, String> whiteList = new HashMap<>();

    public HashMap<Long, String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(HashMap<Long, String> whiteList) {
        this.whiteList = whiteList;
    }

    @Override
    public String toString() {
        return "GetWhiteListResp{" +
                "whiteList=" + whiteList +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
