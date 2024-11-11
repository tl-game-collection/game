package com.xiuxiu.app.protocol.client.room;

import java.util.ArrayList;
import java.util.List;

public class PCLIRoomNtfLocationRelationInfo {
    public static class LocationRelation {
        public long playerUid1;
        public long playerUid2;
        public int distance;

        public LocationRelation() {

        }

        public LocationRelation(long playerUid1, long playerUid2, int distance) {
            this.playerUid1 = playerUid1;
            this.playerUid2 = playerUid2;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return "LocationRelation{" +
                    "playerUid1=" + playerUid1 +
                    ", playerUid2=" + playerUid2 +
                    ", distance=" + distance +
                    '}';
        }
    }
    public List<LocationRelation> location = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIRomNtfLocationRelationInfo{" +
                "location=" + location +
                '}';
    }
}
