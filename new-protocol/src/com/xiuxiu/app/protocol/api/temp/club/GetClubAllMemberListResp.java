package com.xiuxiu.app.protocol.api.temp.club;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取某个club所有玩家成功返回
 * @date 2020/1/10
 * @author luocheng
 */
public class GetClubAllMemberListResp extends ErrorMsg {
    public static class MemberInfo {
        public long playerUid;          //玩家id
        public String playerName;       //玩家昵称
        public long upLinePlayerUid;    //上级id
        public String upLinePlayerName; //上级昵称
        public long gold;               //竞技分
        public long reward;             //奖励分
        public long upGoldTotal;        //总上分
        public long downGoldTotal;      //总下分
        public long joinClubAt;         //入圈时间

        @Override
        public String toString() {
            return "MemberInfo{" +
                    "playerUid=" + playerUid +
                    ", playerName='" + playerName + '\'' +
                    ", upLinePlayerUid=" + upLinePlayerUid +
                    ", upLinePlayerName='" + upLinePlayerName + '\'' +
                    ", gold=" + gold +
                    ", reward=" + reward +
                    ", upGoldTotal=" + upGoldTotal +
                    ", downGoldTotal=" + downGoldTotal +
                    ", joinClubAt=" + joinClubAt +
                    '}';
        }
    }

    public List<MemberInfo> list = new ArrayList<>();
    public int page;
    public int pageSize;
    public boolean next;
    public long clubUid;
    public String clubName;

    @Override
    public String toString() {
        return "GetClubAllMemberListResp{" +
                "list=" + list +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", next=" + next +
                ", clubUid=" + clubUid +
                ", clubName='" + clubName + '\'' +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
