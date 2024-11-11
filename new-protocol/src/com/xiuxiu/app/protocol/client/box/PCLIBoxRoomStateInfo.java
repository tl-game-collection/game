package com.xiuxiu.app.protocol.client.box;

import com.xiuxiu.app.protocol.client.room.PCLIBoxRoomInfo;

public class PCLIBoxRoomStateInfo {
    public long boxUid;         // 包厢uid
    public boolean isStart;     //是否开始
    public int type;            //类型(1自定义玩法桌2固定模式玩法桌3游戏桌)
    public int roomIndex;       // 房间在包厢中索引
    public PCLIBoxRoomInfo roomInfo;
    public int signType;        // 2.2人场机器人桌子 3.3人场机器人桌子, 4.4人场机器人桌子
    public int refreshTime;     //刷新时间
    public long lastRefreshTime;//上次刷新时间戳
    public int boxType;// 0正常包厢1自定义包厢2竞技场包厢3百人场包厢
    public int gameType;// 游戏类型
    public int gameSubType;// 游戏子类型
    public int endPoint; // 游戏底分
    public int playType; // 游戏玩法
    public int endPointMul;// 底分乘数（倍数）
    
    @Override
    public String toString() {
        return "PCLIBoxRoomStateInfo{" +
                "boxUid=" + boxUid +
                ", isStart=" + isStart +
                ", type=" + type +
                ", roomIndex=" + roomIndex +
                ", roomInfo=" + roomInfo +
                ", signType=" + signType +
                ", refreshTime=" + refreshTime +
                ", lastRefreshTime=" + lastRefreshTime +
                '}';
    }
}
