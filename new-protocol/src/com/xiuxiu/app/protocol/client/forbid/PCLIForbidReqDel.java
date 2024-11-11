package com.xiuxiu.app.protocol.client.forbid;

public class PCLIForbidReqDel {
    public long clubUid;        //群uid或联盟uid
    public long uid;

    @Override
    public String toString() {
        return "PCLIForbidReqDel{" +
                ", clubUid=" + clubUid +
                ", uid=" + uid +
                '}';
    }
}
