package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PCLIClubReqRoomCardConvertGoldInfo {
    public long clubUid;            // 俱乐部ID
    public int type;                // 模式 1 日期排序（倒序） 2 玩家兑换详情
    public int page;                // 分页 从0开始
    public boolean next;            // 是否有下一页
    public long count;               // 总数
    public long time;               // 时间

    public List<ConversionRecord> record = new ArrayList<>();

    public static class ConversionRecord {
        public long playerUid;          // 玩家ID
        public String name;             // 玩家名称
        public String icon;             // 玩家头像
        public long value;              // 竞技分
        public long time;               // 时间


        @Override
        public String toString() {
            return "ConversionRecord{" +
                    "playerUid=" + playerUid +
                    ", name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", value=" + value +
                    ", time=" + time +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PCLILeagueNtfConversionRecord{" +
                "clubUid=" + clubUid +
                ", type=" + type +
                ", page=" + page +
                ", record=" + record +
                ", count=" + count +
                ", time=" + time +
                '}';
    }
}
