package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfChangeNameInfo {
    public String newName;

    public PCLIPlayerNtfChangeNameInfo() {

    }

    public PCLIPlayerNtfChangeNameInfo(String newName) {
        this.newName = newName;
    }

    @Override
    public String toString() {
        return "PCLIPlayerReqChangeName{" +
                "newName='" + newName + '\'' +
                '}';
    }
}
