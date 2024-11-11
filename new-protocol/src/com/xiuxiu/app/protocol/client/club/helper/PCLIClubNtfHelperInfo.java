package com.xiuxiu.app.protocol.client.club.helper;

import java.util.ArrayList;
import java.util.List;

public class PCLIClubNtfHelperInfo {
    /** 房间id */
    public int roomId;
    /** 游戏类型 */
    public int gameType;
    /** 当前局数 */
    public int i;
    /** 总局数 */
    public int j;
    /** 玩家列表信息 */
    public List<HelperInfo> players = new ArrayList<>();
    
    /** 当前游戏桌是否开过局 */
    public int state;
    
    public int count;
    public int minCount;
    
    public static class HelperInfo {
        /** 玩家name */
        public String name;
        /** 0: 未准备, 1:准备，2:游戏中 */
        public int state;
    }

    @Override
    public String toString() {
        return "PCLIClubNtfHelperInfo{" +
                "roomId=" + roomId +
                ", gameType=" + gameType +
                '}';
    }
}
