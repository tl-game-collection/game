package com.xiuxiu.app.protocol.api.group;

public class GetGroupCostDetail {
    public long groupUid;
    public int page;
    public int pageSize;

    @Override
    public String toString() {
        return "GetGroupCostDetail{" +
                "groupUid=" + groupUid +
                "page=" + page +
                "pageSize=" + pageSize +
                "}";
    }
}
