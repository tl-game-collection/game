package com.xiuxiu.app.protocol.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPlayerInfo {
    public long uid;
    public String name;
    public String icon;
    public int roomId;
    public long arenaUid;
    public long boxId;
    public long clubUid;
    public long playFieldUid;
    public int gameType;
    public int sex;
    public String zone;
    public HashMap<Integer, Number> money = new HashMap<>();
    public long born;                                   // 出生年月时间戳ms
    public String signature;                            // 个性签名
    public byte emotion;                                // 情感, 0: 保密, 1: 单身, 2: 恋爱中, 3: 已婚, 4:  同性
    public List<String> showImage = new ArrayList<>();  // 展示图片url
    public String cover;                                // 封面图片url
    public long privilege;                              // 权限(按位)1: 添加好友, 2: 创建群
    public String wechat;
    public int registerType;                            // 注册类型
    public String registerParam;                        // 注册参数
    public int isDoneGame;                          // 注册参数
    /**
     * 银行卡号
     */
    public String bankCard;
    /**
     * 银行卡持卡人
     */
    public String bankCardHolder;

    /**
     * 手机号
     */
    public String phone;

    @Override
    public String toString() {
        return "PCLIPlayerInfo{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", roomId=" + roomId +
                ", boxId=" + boxId +
                ", arenaUid=" + arenaUid +
                ", clubUid=" + clubUid +
                ", playFieldUid=" + playFieldUid +
                ", gameType=" + gameType +
                ", sex=" + sex +
                ", zone='" + zone + '\'' +
                ", money=" + money +
                ", born=" + born +
                ", signature='" + signature + '\'' +
                ", emotion=" + emotion +
                ", showImage=" + showImage +
                ", cover='" + cover + '\'' +
                ", privilege=" + privilege +
                ", wechat='" + wechat + '\'' +
                ", registerType=" + registerType +
                ", registerParam='" + registerParam + '\'' +
                ", isDoneGame=" + isDoneGame +
                ", bankCard='" + bankCard + '\'' +
                ", bankCardHolder='" + bankCardHolder + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
