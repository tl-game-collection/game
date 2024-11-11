package com.xiuxiu.app.protocol.client.box;

import java.util.HashMap;

public class PCLIBoxNtfRuleInfo {
    public long groupUid;
    public long boxUid;
    public HashMap<String, Integer> rule = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIBoxNtfRuleInfo{" +
                "groupUid=" + groupUid +
                ", boxUid=" + boxUid +
                ", rule=" + rule +
                '}';
    }
}

