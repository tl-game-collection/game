package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfGameOverInfoByYYMJ extends PCLIMahjongNtfGameOverInfo {
    public List<Byte> niaoList = new ArrayList<>(); // 鸟列表

    @Override
    public String toString() {
        return "PCLIMahjongNtfGameOverInfoByYYMJ{" +
                "allPlayer=" + allPlayer +
                ", bureau=" + bureau +
                ", niaoList=" + niaoList +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
