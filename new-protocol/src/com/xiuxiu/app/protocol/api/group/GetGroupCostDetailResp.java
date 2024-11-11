package com.xiuxiu.app.protocol.api.group;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.List;

public class GetGroupCostDetailResp extends ErrorMsg {
    public Data data;

    @Override
    public String toString() {
        return "GetGroupCostDetailResp{" +
                "ret=" + ret +
                "msg=" + msg +
                "data=" + data +
                "}";
    }

    public static class ArenaCost {
        public long arenaUid;
        public int gameType;
        public int gameSubType;
        public long time;
        public int cost;
        public int bureau;
    }

    public static class Data {
        public long groupUid;
        public int totalCount;
        public List<ArenaCost> list;
    }
}
