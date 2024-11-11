package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfChangeBornInfo {
    public long born;       // 出生年月 时间戳ms

    public PCLIPlayerNtfChangeBornInfo() {

    }

    public PCLIPlayerNtfChangeBornInfo(long born) {
        this.born = born;
    }

    @Override
    public String toString() {
        return "PCLIPlayerNtfChangeBornInfo{" +
                "born=" + born +
                '}';
    }
}
