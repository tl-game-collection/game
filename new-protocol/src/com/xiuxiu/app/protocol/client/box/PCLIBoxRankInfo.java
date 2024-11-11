package com.xiuxiu.app.protocol.client.box;

public class PCLIBoxRankInfo {
    public long playerUid;
    public String playerName;
    public String playerIcon;
    public int bureau;
    public int winBureau;

    @Override
    public String toString() {
        return "PCLIBoxRankInfo{" +
                "playerUid=" + playerUid +
                ", playerName='" + playerName + '\'' +
                ", playerIcon='" + playerIcon + '\'' +
                ", bureau=" + bureau +
                ", winBureau=" + winBureau +
                '}';
    }
}
