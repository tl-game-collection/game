package com.xiuxiu.app.protocol.api.temp.player;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetOwnerClubInfoResp extends ErrorMsg {
    public static class FloorInfo {
        public long floorUid;
        public int gameDesk2Min;
        public int gameDesk2Max;
        public int randomTime2;
        public int gameDesk3Min;
        public int gameDesk3Max;
        public int randomTime3;

        @Override
        public String toString() {
            return "FloorInfo{" +
                    "floorUid=" + floorUid +
                    ", gameDesk2Min=" + gameDesk2Min +
                    ", gameDesk2Max=" + gameDesk2Max +
                    ", randomTime2=" + randomTime2 +
                    ", gameDesk3Min=" + gameDesk3Min +
                    ", gameDesk3Max=" + gameDesk3Max +
                    ", randomTime3=" + randomTime3 +
                    '}';
        }
    }
    public static class ClubInfo {
        public long uid;
        public String name;
        public long ownerUid;
        public List<FloorInfo> floorInfos = new ArrayList<>();

        @Override
        public String toString() {
            return "ClubInfo{" +
                    "uid=" + uid +
                    ", name='" + name + '\'' +
                    ", ownerUid=" + ownerUid +
                    ", floorInfos=" + floorInfos +
                    '}';
        }
    }

    public List<ClubInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "GetOwnerClubInfoResp{" +
                "list=" + list +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
