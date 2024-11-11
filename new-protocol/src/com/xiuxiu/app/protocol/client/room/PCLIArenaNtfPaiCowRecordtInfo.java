package com.xiuxiu.app.protocol.client.room;

import com.xiuxiu.app.protocol.client.PCLIPaiCowScoreInfo;

import java.util.ArrayList;
import java.util.List;

public class PCLIArenaNtfPaiCowRecordtInfo {
    
    public int page;        // 从0开始
    public boolean next;    // 是否还有下一页
    public List<PCLIPaiCowScoreInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIArenaNtfCowRecordtInfo{" +
                ", page=" + page +
                ", next=" + next +
                ", list=" + list +
                '}';
    }
}
