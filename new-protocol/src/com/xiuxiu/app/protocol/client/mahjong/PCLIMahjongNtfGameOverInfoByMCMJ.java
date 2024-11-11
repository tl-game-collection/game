package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfGameOverInfoByMCMJ extends PCLIMahjongNtfGameOverInfo {
    public List<Integer> cunList = new ArrayList<>(); // 存的列表，0x04风翻，0x10将翻，0x40连宝翻，0x80豹子翻
    public List<Integer> fanList = new ArrayList<>(); // 当局翻的列表，数值意义同cunList

    @Override
    public String toString() {
        return "PCLIMahjongNtfGameOverInfoByMCMJ{" +
                "cunList=" + cunList +
                ", fanList=" + fanList +
                ", allPlayer=" + allPlayer +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
