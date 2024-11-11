package com.xiuxiu.app.protocol.client.forbid;

public class PCLIForbidReqSearchList {
    public String search;   // 查询内容
    public long clubUid;        //俱乐部id

    @Override
    public String toString() {
        return "PCLIForbidReqSearchList{" +
                "search=" + search +
                "clubUid=" + clubUid +
                '}';
    }
}
