package com.xiuxiu.app.protocol.client.club;

public class PCLIClubNtfActivityDivideInfoItem {

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
