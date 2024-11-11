package com.xiuxiu.app.protocol.client.box;

import java.util.HashMap;

public class PCLIBoxReqModifyBoxRuleInfo {
    public long clubUid;       // 群uid
    public long boxUid;         // 包厢uid
//    public int gameType;        // 游戏类型
//    public int gameSubType;     // 游戏子类型
//    public HashMap<String, Integer> rule = new HashMap<>();  // 规则, 根据配置表来
    public HashMap<String, String> extra = new HashMap<>();  // 额外, 根据配置表来

    @Override
    public String toString() {
        return "PCLIBoxReqModifyBoxRuleInfo{" +
                "clubUid=" + clubUid +
                ", boxUid=" + boxUid +
                ", extra=" + extra +
                '}';
    }
}
