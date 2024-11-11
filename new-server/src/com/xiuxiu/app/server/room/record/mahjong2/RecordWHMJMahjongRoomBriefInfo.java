package com.xiuxiu.app.server.room.record.mahjong2;

public class RecordWHMJMahjongRoomBriefInfo extends RecordMahjongRoomBriefInfo {
    protected boolean isBaoZiF = false;                // 豹子翻倍
    protected boolean isJFYLF = false;                 // 见风原癞翻倍
    protected boolean isJ258F = false;                 // 见258将翻倍

    public boolean isBaoZiF() {
        return isBaoZiF;
    }

    public void setBaoZiF(boolean baoZiF) {
        isBaoZiF = baoZiF;
    }

    public boolean isJFYLF() {
        return isJFYLF;
    }

    public void setJFYLF(boolean JFYLF) {
        isJFYLF = JFYLF;
    }

    public boolean isJ258F() {
        return isJ258F;
    }

    public void setJ258F(boolean j258F) {
        isJ258F = j258F;
    }
}
