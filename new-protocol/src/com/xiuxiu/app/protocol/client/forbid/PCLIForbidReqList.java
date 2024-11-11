package com.xiuxiu.app.protocol.client.forbid;

public class PCLIForbidReqList {
    public long clubUid;        //亲友圈uid
    public int page;            //请求页数

    @Override
    public String toString() {
        return "PCLIForbidReqList{" +
                "clubUid=" + clubUid +
                ", page=" + page +
                '}';
    }
}
