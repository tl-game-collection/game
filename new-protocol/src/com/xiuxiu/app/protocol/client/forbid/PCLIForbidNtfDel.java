package com.xiuxiu.app.protocol.client.forbid;

public class PCLIForbidNtfDel {
    public long clubUid;        //群uid或联盟uid
    public long uid;

    @Override
    public String toString() {
        return "PCLIForbidNtfDel{" +
                ", clubUid=" + clubUid +
                ", uid=" + uid +
                '}';
    }
}
