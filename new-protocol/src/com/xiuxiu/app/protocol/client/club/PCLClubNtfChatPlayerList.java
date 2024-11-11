package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

public class PCLClubNtfChatPlayerList {
    public static class playerInfo {
        public long uid; //玩家uid
        public String name; //玩家名字
        public String icon; //玩家头像icon
        public int jobType;  //玩家是否是我的直属下级如果是下级就传6不是0
        public int jobType2; //唐文照用...1,盟主。2,圈主，3，副圈主，4,本圈管理 5，上级(作废)，6，下级(作废)，7，圈内成员(自己首先是圈主或副圈主) 0，默认没有级别关联
        public int jobType3; //唐文照用...0.无 5.上级 6.下级
        public String bankCard;//银行卡号
        public String bankCardHolder;//银行卡持卡人
        public List<String> showImage = new ArrayList<>();//展示图片
        public long toGroupUid = -1;//给哪个群里的人发（总圈主才用的到）
        public long score = 0;//竞技分
        public boolean setGoldUpLine;   //是否是上级上下分
        public boolean isOnline;        //是否在线
        public boolean isUpGoldTreasuer;//是否是上分财务

        @Override
        public String toString() {
            return "playerInfo{" +
                    "uid=" + uid +
                    ", name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", jobType=" + jobType +
                    ", jobType2=" + jobType2 +
                    ", jobType3=" + jobType3 +
                    ", bankCard='" + bankCard + '\'' +
                    ", bankCardHolder='" + bankCardHolder + '\'' +
                    ", showImage=" + showImage +
                    ", toGroupUid=" + toGroupUid +
                    ", score=" + score +
                    ", setGoldUpLine=" + setGoldUpLine +
                    ", isOnline=" + isOnline +
                    ", isUpGoldTreasuer=" + isUpGoldTreasuer +
                    '}';
        }
    }

    public List<playerInfo> ownerList = new ArrayList<>();//圈主列表
    public List<playerInfo> selfClubList = new ArrayList<>();//本圈列表
    public long mainClubUid = -1;                                //总圈id
    public long mainClubOwnerUid = -1;                           //总圈主id
    public String mainClubOwnerName;
    public String mainClubOwnerIcon;
    public List<String> mainClubOwnerShowImage = new ArrayList<>();//总圈主展示图片
    public long mainClubOwnerGold;                               //总圈主金币
    public boolean mainClubOwnerIsOnline;                        //总圈主是否在线
    public String mainClubOwnerBankCard;                        //总圈主银行卡号
    public String mainClubOwnerBankCardHolder;                  //总圈主银行卡持卡人
    public boolean isShowOwnerList;                              //是否显示圈主列表
    public int page;
    public boolean next;

    @Override
    public String toString() {
        return "PCLClubNtfChatPlayerList{" +
                "ownerList=" + ownerList +
                ", selfClubList=" + selfClubList +
                ", mainClubUid=" + mainClubUid +
                ", mainClubOwnerUid=" + mainClubOwnerUid +
                ", mainClubOwnerName='" + mainClubOwnerName + '\'' +
                ", mainClubOwnerIcon='" + mainClubOwnerIcon + '\'' +
                ", mainClubOwnerShowImage=" + mainClubOwnerShowImage +
                ", mainClubOwnerGold=" + mainClubOwnerGold +
                ", mainClubOwnerIsOnline=" + mainClubOwnerIsOnline +
                ", mainClubOwnerBankCard='" + mainClubOwnerBankCard + '\'' +
                ", mainClubOwnerBankCardHolder='" + mainClubOwnerBankCardHolder + '\'' +
                ", isShowOwnerList=" + isShowOwnerList +
                ", page=" + page +
                ", next=" + next +
                '}';
    }
}
