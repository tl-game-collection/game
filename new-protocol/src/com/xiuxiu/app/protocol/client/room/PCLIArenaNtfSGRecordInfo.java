package com.xiuxiu.app.protocol.client.room;

import com.xiuxiu.app.protocol.client.PCLSGScoreInfo;

import java.util.ArrayList;
import java.util.List;

public class PCLIArenaNtfSGRecordInfo {
    public int page;        // 从0开始
    public boolean next;    // 是否还有下一页
    public List<PCLSGScoreInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIArenaNtfSGRecordInfo{" +
                ", page=" + page +
                ", next=" + next +
                ", list=" + list +
                '}';
    }
}
