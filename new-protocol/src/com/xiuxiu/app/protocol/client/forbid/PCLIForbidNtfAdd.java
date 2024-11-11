package com.xiuxiu.app.protocol.client.forbid;

public class PCLIForbidNtfAdd {
    public long clubUid;        //群uid或联盟uid
    public PCLIForbidNtfInfo info; //新的屏蔽关系

    @Override
    public String toString() {
        return "PCLIForbidNtfAdd{" +
                ", clubUid=" + clubUid +
                ", info=" + info +
                '}';
    }
}
