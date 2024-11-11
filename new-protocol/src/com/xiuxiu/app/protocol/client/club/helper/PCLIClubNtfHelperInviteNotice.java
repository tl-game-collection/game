package com.xiuxiu.app.protocol.client.club.helper;

import java.util.HashMap;

public class PCLIClubNtfHelperInviteNotice {
    public long clubUid; // 亲友圈uid
    public String clubName; // 亲友圈名称
    public int type;// 亲友圈类型1房卡2金币
    public int gameType;         // 游戏类型
    public int gameSubType;      // 游戏子类型
    public HashMap<String, Integer> rule = new HashMap<>();
    public long roomId;//房间id
    
    public String name;//邀请人名称
    public String icon;//邀请人头像
    public int status;//被邀请人是否接受(0拒绝1接受)

    @Override
    public String toString() {
        return "PCLIClubNtfHelperInviteNotice{" + "clubUid=" + clubUid + '}';
    }
}
