package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfCanFumbleInfo {
    public long uid;
    public boolean isFumbleOnBar = false;                   // 是否是杠后摸牌

    public PCLIMahjongNtfCanFumbleInfo(long uid, boolean isFumbleOnBar) {
        this.uid = uid;
        this.isFumbleOnBar = isFumbleOnBar;
    }

    @Override
    public String toString() {
        return "PCLIMahjongNtfCanFumbleInfo{" +
                "uid=" + uid +
                ", isFumbleOnBar=" + isFumbleOnBar +
                '}';
    }
}
