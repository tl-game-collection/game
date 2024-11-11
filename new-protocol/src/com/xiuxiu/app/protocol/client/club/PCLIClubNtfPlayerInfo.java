package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PCLIClubNtfPlayerInfo {
    public long playerUid; //玩家uid
    public long clubUid;  //所在圈id
    public String name; //玩家名字
    public String icon; //玩家头像icon
    public long score;  //竞技分
    public int jobType;
    public int jobType2;//唐文照用...1,盟主。2,圈主，3，副圈主，4,本圈管理 5，上级，6，下级，7，圈内成员(自己首先是圈主或副圈主)0，默认没有级别关联
    public int jobType3;//唐文照用...0.无 5.上级 6.下级
    public int divide;//直属奖励分
    public int divideLine;//一条线奖励分
    public String bankCard;//银行卡号
    public String bankCardHolder;//银行卡持卡人
    public List<String> showImage = new ArrayList<>();//展示图片
    public boolean setGoldUpLine;       //在圈中是否是上级上下分
    public boolean isOnline;        //是否在线
    public boolean isUpGoldTreasuer;//是否是上分财务
    public AtomicInteger subordinateCount= new AtomicInteger();//直属下级人数

    @Override
    public String toString() {
        return "PCLIClubNtfPlayerInfo{" +
                "playerUid=" + playerUid +
                ", clubUid=" + clubUid +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", score=" + score +
                ", jobType=" + jobType +
                ", jobType2=" + jobType2 +
                ", jobType3=" + jobType3 +
                ", divide=" + divide +
                ", divideLine=" + divideLine +
                ", bankCard='" + bankCard + '\'' +
                ", bankCardHolder='" + bankCardHolder + '\'' +
                ", showImage=" + showImage +
                ", setGoldUpLine=" + setGoldUpLine +
                ", isOnline=" + isOnline +
                ", isUpGoldTreasuer=" + isUpGoldTreasuer +
                ", subordinateCount=" + subordinateCount +
                '}';
    }
}
