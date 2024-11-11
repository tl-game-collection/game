package com.xiuxiu.app.protocol.client.room;

import com.xiuxiu.app.protocol.client.PCLICowScoreInfo;

import java.util.ArrayList;
import java.util.List;

public class PCLIArenaNtfCowRecordtInfo {
    public int page;        // 从0开始
    public boolean next;    // 是否还有下一页
    public List<PCLICowScoreInfo> list = new ArrayList<>();
    
    @Override
    public String toString() {
        return "PCLIArenaNtfCowRecordtInfo{" +
                ", page=" + page +
                ", next=" + next +
                ", list=" + list +
                '}';
    }
}
