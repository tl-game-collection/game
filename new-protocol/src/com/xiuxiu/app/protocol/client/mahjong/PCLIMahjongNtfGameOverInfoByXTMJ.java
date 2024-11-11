package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfGameOverInfoByXTMJ extends PCLIMahjongNtfGameOverInfo {
    public List<Byte> horseList = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIMahjongNtfGameOverInfoByXTMJ{" +
                "horseList=" + horseList +
                ", allPlayer=" + allPlayer +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
