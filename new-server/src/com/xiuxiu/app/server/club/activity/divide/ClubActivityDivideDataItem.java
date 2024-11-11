package com.xiuxiu.app.server.club.activity.divide;

import com.xiuxiu.app.protocol.client.club.PCLIClubNtfActivityDivideInfoItem;

/**
 * 联盟奖励分成比例每个档位数据
 * 
 * @author Administrator
 *
 */
public class ClubActivityDivideDataItem {

    /**
     * 需要满足xx返利
     */
    private long needValue;

    /**
     * 获取分成比例-成员
     */
    private int member;
    /**
     * 获取分成比例-一条线
     */
    private int line;

    public static ClubActivityDivideDataItem valueOf(PCLIClubNtfActivityDivideInfoItem tempItem) {
        ClubActivityDivideDataItem item = new ClubActivityDivideDataItem();
        item.setLine(Math.min(tempItem.getLine(), 100));
        item.setMember(Math.min(tempItem.getMember(), 100));
        item.setNeedValue(tempItem.getNeedValue());
        return item;
    }

    public long getNeedValue() {
        return needValue;
    }

    public void setNeedValue(long needValue) {
        this.needValue = needValue;
    }

    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

}
