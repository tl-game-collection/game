package com.xiuxiu.app.protocol.client.hundred;

import java.util.HashMap;

/**
 * 百人场请求上庄
 */
public class PCLIHundredReqUpBanker {
    public long roomId;
    public HashMap<String, Integer> param = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIHundredReqUpBanker{" +
                "roomId=" + roomId +
                ", param=" + param +
                '}';
    }
}
