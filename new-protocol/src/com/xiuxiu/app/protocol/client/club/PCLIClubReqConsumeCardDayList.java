package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqConsumeCardDayList {
    /**
     * 亲友圈uid
     */
    public long id;
    /** 当前页码 */
    public int page;
    /** 每页数量 */
    public int size;
    /** 日期 */
    public String time;

    @Override
    public String toString() {
        return "PCLIClubReqConsumeCardDayList{" + ",id=" + id + ",page=" + page + ", size=" + size + '}';
    }
}
