package com.xiuxiu.app.protocol.client.room;

import com.xiuxiu.app.protocol.client.PCLIFgfScoreInfo;
import com.xiuxiu.app.protocol.client.PCLIScoreInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIArenaNtfFgfRecordtInfo {
    public int page;        // 从0开始
    public boolean next;    // 是否还有下一页
    public List<PCLIFgfScoreInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIArenaNtfFgfRecordtInfo{" +
                ", page=" + page +
                ", next=" + next +
                ", list=" + list +
                '}';
    }
}
