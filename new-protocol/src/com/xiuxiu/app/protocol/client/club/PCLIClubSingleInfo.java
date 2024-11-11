package com.xiuxiu.app.protocol.client.club;

import com.xiuxiu.app.protocol.client.PCLIPlayerBriefInfo;

import java.util.ArrayList;
import java.util.List;

public class PCLIClubSingleInfo {
    public long clubUid;                                        // 俱乐部Uid
    public long ownerUid;                                       // 俱乐部拥有者ID
    public String name;                                         // 俱乐部名称
    public String icon;                                         // 俱乐部头像
    public String desc;                                         // 俱乐部描述
    public long createTime;                                     // 俱乐部创建时间
    public String gameDesc;                                     // 俱乐部游戏描述
    public PCLIPlayerBriefInfo creater;                         // 创建者的信息
    public int clubType;                                        // 俱乐部类型 1 房卡亲友圈 2 金币亲友圈
    public int totalCostDiamond;                                // 总消耗钻石
    public String totalServiceCharge;                           // 总服务费
    public List<String> groupMemberIcon = new ArrayList<>();    // 群成员头像
    public PCLIClubMemberInfo myMemberInfo;                     // 自己信息
    public int memberCnt;                                       // 成员数量
    public long parentUid;                                      // 上级uid
    public List<Long> childUidList = new ArrayList<>();         // 下级uid链表
    public boolean open;                                        //活动是否开启
    /** 打烊状态,0未打烊1打烊中2已打烊 */
    public int status;

    @Override
    public String toString() {
        return "PCLIClubSingleInfo{" +
                "clubUid=" + clubUid +
                ", ownerUid=" + ownerUid +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", desc='" + desc + '\'' +
                ", createTime=" + createTime +
                ", gameDesc=" + gameDesc +
                ", clubType=" + clubType +
                ", creater=" + creater +
                ", totalCostDiamond=" + totalCostDiamond +
                ", myMemberInfo=" + myMemberInfo +
                ", totalServiceCharge='" + totalServiceCharge + '\'' +
                ", groupMemberIcon=" + groupMemberIcon +
                ", memberCnt=" + memberCnt +
                ", parentUid=" + parentUid +
                ", childUidList=" + childUidList +
                ", open=" + open +
                '}';
    }
}
