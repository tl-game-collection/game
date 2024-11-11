package com.xiuxiu.app.protocol.client.club;

import java.util.List;

public class PCLIClubReqSetLikeGame {
    public long clubUid; // 俱乐部uid
    public int gameType; // 游戏主类型
    public List<Integer> gameSubTypes; // 是否允许设置上级上下分(1允许，0不允许)

    @Override
    public String toString() {
        return "PCLIClubReqSetGoldUpLine{" + "clubUid=" + clubUid + ", gameType=" + gameType + ", gameSubTypes=" + gameSubTypes + '}';
    }
}
