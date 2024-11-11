package com.xiuxiu.app.protocol.client.room;

import com.xiuxiu.app.protocol.client.PCLICowScoreInfo;
import com.xiuxiu.app.protocol.client.PCLIThirteenScoreInfo;

import java.util.ArrayList;
import java.util.List;

public class PCLIArenaNtfThirteenRecordtInfo {
    public int page;        // 从0开始
    public boolean next;    // 是否还有下一页
    public List<PCLIThirteenScoreInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIArenaNtfThirteenRecordtInfo{" +
                ", page=" + page +
                ", next=" + next +
                ", list=" + list +
                '}';
    }
}
