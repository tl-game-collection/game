package com.xiuxiu.app.server.statistics;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;

public class LogAccountRemain extends BaseTable {
    private long date;          // 日期零点时间戳 单位：秒
    private long registerNum;   // 注册人数
    private int day_2;
    private int day_3;
    private int day_4;
    private int day_5;
    private int day_6;
    private int day_7;
    private int day_14;
    private int day_30;

    public LogAccountRemain() {
        this.tableType = ETableType.TB_LOG_ACCOUNT_REMAIN;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getRegisterNum() {
        return registerNum;
    }

    public void setRegisterNum(long registerNum) {
        this.registerNum = registerNum;
    }

    public int getDay_2() {
        return day_2;
    }

    public void setDay_2(int day_2) {
        this.day_2 = day_2;
    }

    public int getDay_3() {
        return day_3;
    }

    public void setDay_3(int day_3) {
        this.day_3 = day_3;
    }

    public int getDay_4() {
        return day_4;
    }

    public void setDay_4(int day_4) {
        this.day_4 = day_4;
    }

    public int getDay_5() {
        return day_5;
    }

    public void setDay_5(int day_5) {
        this.day_5 = day_5;
    }

    public int getDay_6() {
        return day_6;
    }

    public void setDay_6(int day_6) {
        this.day_6 = day_6;
    }

    public int getDay_7() {
        return day_7;
    }

    public void setDay_7(int day_7) {
        this.day_7 = day_7;
    }

    public int getDay_14() {
        return day_14;
    }

    public void setDay_14(int day_14) {
        this.day_14 = day_14;
    }

    public int getDay_30() {
        return day_30;
    }

    public void setDay_30(int day_30) {
        this.day_30 = day_30;
    }

    @Override
    public String toString() {
        return "LogAccountRemain{" +
                "date=" + date +
                ", registerNum=" + registerNum +
                ", day_2=" + day_2 +
                ", day_3=" + day_3 +
                ", day_4=" + day_4 +
                ", day_5=" + day_5 +
                ", day_6=" + day_6 +
                ", day_7=" + day_7 +
                ", day_14=" + day_14 +
                ", day_30=" + day_30 +
                ", isNew=" + isNew +
                ", tableType=" + tableType +
                ", uid=" + uid +
                ", dirty=" + dirty +
                '}';
    }
}
