package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqConsumeCardList {
    /**
     * 亲友圈uid
     */
    public long id;
    /** 当前页码 */
    public int page;
    /** 每页数量 */
    public int size;

    @Override
    public String toString() {
        return "PCLIClubReqConsumeCardList{" + ",id=" + id + ",page=" + page + ", size=" + size + '}';
    }
}
