package com.xiuxiu.app.protocol.client.club;

import com.xiuxiu.app.protocol.client.player.PCLIPlayerSmallInfo;

/**
 *
 */
public class PCLIClubBriefInfo {
    public long uid;                    // 俱乐部ID
    public String name;                 // 俱乐部名称
    public String icon;                 // 俱乐部头像
    public String desc;                 // 俱乐部描述
    public String gameDesc;             // 俱乐部游戏描述
    public long createTime;             // 俱乐部创建时间
    public int clubType;                // 俱乐部类型
    public PCLIPlayerSmallInfo founder; // 俱乐部创始人信息

    @Override
    public String toString() {
        return "PCLIClubBriefInfo{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", desc='" + desc + '\'' +
                ", gameDesc='" + gameDesc + '\'' +
                ", createTime=" + createTime +
                ", clubType=" + clubType +
                ", founder=" + founder +
                '}';
    }
}
