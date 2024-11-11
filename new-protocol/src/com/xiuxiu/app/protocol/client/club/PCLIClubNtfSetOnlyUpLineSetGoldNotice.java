package com.xiuxiu.app.protocol.client.club;

public class PCLIClubNtfSetOnlyUpLineSetGoldNotice {

    public long clubUid;
    public long uid; // 玩家uid
    public boolean flag; // 是否允许设置上级上下分(true允许，false不允许)

    @Override
    public String toString() {
        return "PCLIClubNtfSetGoldUpLine{" + "uid=" + uid + ", flag=" + flag + '}';
    }
}
