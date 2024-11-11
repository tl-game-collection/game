package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerNtfRecommendInfo {
    public int diamond;                 // 推荐获得的钻石
    public int num;                     // 推荐用户数量
    public long recommendPlayerUid;     // 推荐用户Uid
    public String recommendPlayerName;  // 推荐用户name
    public String recommendPlayerIcon;  // 推荐用户icon

    @Override
    public String toString() {
        return "PCLIPlayerNtfRecommendInfo{" +
                "diamond=" + diamond +
                ", num=" + num +
                ", recommendPlayerUid=" + recommendPlayerUid +
                ", recommendPlayerName='" + recommendPlayerName + '\'' +
                ", recommendPlayerIcon='" + recommendPlayerIcon + '\'' +
                '}';
    }
}
