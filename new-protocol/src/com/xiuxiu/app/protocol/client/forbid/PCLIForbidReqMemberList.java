package com.xiuxiu.app.protocol.client.forbid;

import java.util.List;

public class PCLIForbidReqMemberList {
    public long clubUid;        //亲友圈uid
    public int page;

    @Override
    public String toString() {
        return "PCLIForbidReqAdd{" +
                ", clubUid=" + clubUid +
                ", page=" + page +
                '}';
    }
}
