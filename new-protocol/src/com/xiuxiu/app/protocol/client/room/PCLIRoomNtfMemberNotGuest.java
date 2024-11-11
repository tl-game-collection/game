package com.xiuxiu.app.protocol.client.room;

import java.util.HashSet;

public class PCLIRoomNtfMemberNotGuest {
    public int roomId;
    public HashSet<Long> notGuestMembers = new HashSet<>();

    @Override
    public String toString() {
        return "PCLIRoomNtfMemberNotGuest{" +
                "roomId=" + roomId +
                ", notGuestMembers=" + notGuestMembers +
                '}';
    }
}
