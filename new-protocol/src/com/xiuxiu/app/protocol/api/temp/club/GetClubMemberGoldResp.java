package com.xiuxiu.app.protocol.api.temp.club;

import com.xiuxiu.core.net.protocol.ErrorMsg;

/**
 * 获取群玩家竞技值成功返回
 * @auther: luocheng
 * @date: 2019/12/27 10:40
 */
public class GetClubMemberGoldResp extends ErrorMsg {
    public long clubUid;
    public String clubName;
    public long playerUid;
    public String playerName;
    public long upLineUid;
    public String upLineName;
    public long gold;//竞技值

    @Override
    public String toString() {
        return "GetClubMemberGoldResp{" +
                "clubUid=" + clubUid +
                ", clubName='" + clubName + '\'' +
                ", playerUid=" + playerUid +
                ", playerName='" + playerName + '\'' +
                ", upLineUid=" + upLineUid +
                ", upLineName='" + upLineName + '\'' +
                ", gold=" + gold +
                '}';
    }
}
