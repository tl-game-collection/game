package com.xiuxiu.app.protocol.client.club;

/**
 *
 */
public class PCLIClubReqRoomCardConvertGoldRecord {
    public long clubUid;            // 俱乐部ID
    public int type;                // 模式 1 日期排序（倒序） 2 玩家兑换详情
    public int page;                // 分页, 从0开始.
    public long time;               // 时间

    @Override
    public String toString() {
        return "PCLIClubReqRoomCardConvertGoldRecord{" +
                "clubUid=" + clubUid +
                ", type=" + type +
                ", page=" + page +
                ", time=" + time +
                '}';
    }
}
