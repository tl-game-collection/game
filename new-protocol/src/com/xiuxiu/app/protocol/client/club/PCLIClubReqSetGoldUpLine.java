package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqSetGoldUpLine {
    public long clubUid; // 俱乐部uid
    public long uid; // 玩家uid
    public boolean flag; // 是否允许设置上级上下分(true允许，false不允许)

    @Override
    public String toString() {
        return "PCLIClubReqSetGoldUpLine{" + "clubUid=" + clubUid + ", uid=" + uid + ", flag=" + flag + '}';
    }
}
