package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongReqShuKanInfo {
    public List<Integer> point =new ArrayList<>();           // 1-9点

    @Override
    public String toString() {
        return "PCLIMahjongReqShuKanInfo{" +
                ", point=" + point +
                '}';
    }
}