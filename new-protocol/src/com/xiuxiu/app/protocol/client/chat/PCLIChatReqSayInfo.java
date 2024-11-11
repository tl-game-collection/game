package com.xiuxiu.app.protocol.client.chat;

public class PCLIChatReqSayInfo {
    public long opaque;            // 该次请求标识
    public int messageType;        // 消息类型, 1:玩家 2:群 3,竞技场 4，客服 5，百人场
    public long toUid;             // 目标Uid
    public int contentType;        // 内容类型, 0:文字, 1:图片, 2:语音
    public String message;         // 消息内容, 如果是语音bytes需要Base64
    public long arenaUid;          // 竞技场ID
    public long leagueUid;         //联盟uid
    public long groupUid;          //clubUid
    public long toGroupUid;        //给哪个clubUid发消息

    @Override
    public String toString() {
        return "PCLIChatReqSayInfo{" +
                "opaque=" + opaque +
                ", messageType=" + messageType +
                ", toUid=" + toUid +
                ", contentType=" + contentType +
                ", message='" + message + '\'' +
                ", arenaUid=" + arenaUid +
                ", leagueUid=" + leagueUid +
                ", groupUid=" + groupUid +
                ", toGroupUid=" + toGroupUid +
                '}';
    }
}
