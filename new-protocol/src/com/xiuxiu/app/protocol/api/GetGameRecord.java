package com.xiuxiu.app.protocol.api;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取游戏战绩
 * @auther: luocheng
 * @date: 2019/12/28 18:40
 */
public class GetGameRecord {
    //必填
    public int roomType; //大厅、亲友圈和比赛场、百人场
    public long time;   //某一天时间0点
    public long playerUid;//至少1个玩家uid
    public int page;
    public int pageSize;

    //选填
    public int gameType;
    public long roomId;
    public long playerUid2;

    public String sign;         // md5(roomType + time + playerUid + page + pageSize + gameType + roomId + playerUid2 + key)

    @Override
    public String toString() {
        return "GetGameRecord{" +
                "roomType=" + roomType +
                ", time=" + time +
                ", playerUid=" + playerUid +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", gameType=" + gameType +
                ", roomId=" + roomId +
                ", playerUid2=" + playerUid2 +
                ", sign='" + sign + '\'' +
                '}';
    }
}
