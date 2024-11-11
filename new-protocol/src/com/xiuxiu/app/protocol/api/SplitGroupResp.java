package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class SplitGroupResp extends ErrorMsg {
    public static class SplitGroupInfo{
        public long fromGroupUid;                       // 拆分群Uid
        public long newGroupUid;                        // 新群Uid

        @Override
        public String toString() {
            return "SplitGroupInfo{" +
                    "fromGroupUid=" + fromGroupUid +
                    ", newGroupUid=" + newGroupUid +
                    '}';
        }
    }

    public SplitGroupInfo data;

    @Override
    public String toString() {
        return "SplitGroupResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
