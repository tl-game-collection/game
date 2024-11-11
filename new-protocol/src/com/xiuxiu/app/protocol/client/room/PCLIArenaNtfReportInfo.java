package com.xiuxiu.app.protocol.client.room;

import com.xiuxiu.app.protocol.client.PCLIScoreInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIArenaNtfReportInfo {
    public long boxUid;
    public int page;        // 从0开始
    public boolean next;    // 是否还有下一页

    public HashMap<String, Integer> allCnt;// 所有计数
    public String score;
    public long reportUid;
    public int bureau;
    public List<PCLIScoreInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIArenaNtfReportInfo{" +
                "boxUid=" + boxUid +
                ", page=" + page +
                ", next=" + next +
                ", allCnt=" + allCnt +
                ", score=" + score +
                ", reportUid=" + reportUid +
                ", bureau=" + bureau +
                ", list=" + list +
                '}';
    }
}
