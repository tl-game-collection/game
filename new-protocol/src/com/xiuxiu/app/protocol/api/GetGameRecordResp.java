package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取游戏战绩成功返回
 * @auther: luocheng
 * @date: 2019/12/28 18:40
 */
public class GetGameRecordResp extends ErrorMsg {
    public static class PlayerInfo {
        public long playerUid;
        public String name;
        public int score;

        @Override
        public String toString() {
            return "PlayerInfo{" +
                    "playerUid=" + playerUid +
                    ", name='" + name + '\'' +
                    ", score=" + score +
                    '}';
        }
    }

    public static class GameRecordInfo {
        public int gameType;
        public long endTime;
        public long roomId;
        public long clubUid;
        public List<PlayerInfo> playerInfo = new ArrayList<>();

        @Override
        public String toString() {
            return "GameRecordInfo{" +
                    "gameType=" + gameType +
                    ", endTime=" + endTime +
                    ", roomId=" + roomId +
                    ", clubUid=" + clubUid +
                    ", playerInfo=" + playerInfo +
                    '}';
        }
    }

    public int roomType;
    public List<GameRecordInfo> gameRecordInfo = new ArrayList<>();

    @Override
    public String toString() {
        return "GetGameRecordResp{" +
                "roomType=" + roomType +
                ", gameRecordInfo=" + gameRecordInfo +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
