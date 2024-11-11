package com.xiuxiu.app.protocol.client.club;

import java.util.List;

public class PCLIClubConsumeCardDayList {
    /** 当前页 */
    public int page;
    /** 总页 */
    public int totalPage;
    /** 数据集合 */
    public List<PCLIClubConsumeCard> data;

    @Override
    public String toString() {
        return "PCLIClubConsumeCardList{" + "totalPage=" + totalPage + ", page=" + page + '}';
    }
}
