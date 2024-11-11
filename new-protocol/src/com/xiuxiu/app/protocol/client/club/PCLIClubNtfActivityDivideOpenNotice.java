package com.xiuxiu.app.protocol.client.club;

public class PCLIClubNtfActivityDivideOpenNotice {
    /**
     * 是否开启(true开启false关闭)
     */
    public boolean open;
    /**
     *群id
     */
    public long clubUid;

    @Override
    public String toString() {
        return "PCLILeagueNtfGetRewardValueDivide{" + ",open=" + open + ", clubUid=" + clubUid + '}';
    }
}
