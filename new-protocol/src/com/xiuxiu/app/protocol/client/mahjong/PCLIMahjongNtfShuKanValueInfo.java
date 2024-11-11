package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfShuKanValueInfo {
    public long playerUid;
    public List<Integer> value=new ArrayList<>();       // 高16位258将, 低16为1-9点数*

    @Override
    public String toString() {
        return "PCLIMahjongNtfShuKanValueInfo{" +
                "playerUid=" + playerUid +
                ", value=" + value +
                '}';
    }
}
