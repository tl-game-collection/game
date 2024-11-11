package com.xiuxiu.app.protocol.api.temp.club;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetClubExpendResp extends ErrorMsg {
    public static class clubExpend {
        public int expend;
        public String time;

        @Override
        public String toString() {
            return "clubExpend{" +
                    "expend=" + expend +
                    ", time=" + time +
                    '}';
        }
    }
    public long clubUid;
    public String clubName;
    public int page;
    public boolean next;
    public List<clubExpend> list = new ArrayList<>();

    @Override
    public String toString() {
        return "GetClubExpendResp{" +
                "clubUid=" + clubUid +
                ", clubName='" + clubName + '\'' +
                ", page=" + page +
                ", next=" + next +
                ", list=" + list +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
