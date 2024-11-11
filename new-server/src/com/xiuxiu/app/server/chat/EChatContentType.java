package com.xiuxiu.app.server.chat;

public enum EChatContentType {
    NORMAL(null),
    IMAGE("[图片]"),
    VOICE("[语音]"),
    OPEN_ROOM("[开启房间]"),
    SHARE_BOX_SCORE("[包间总战绩分享]"),
    TIP(null),
    OPEN_ARENA("[开启竞技场]"),
    VISIT_FRIEND("[好友名片]"),
    VISIT_GROUP("[群名片]"),
    OPEN_BOX("[开启包间]"),
    SHARE_ROOM("[房间分享]"),
    SHARE_ROOM_SCORE("[房间总战绩分享]"),
    SHARE_ARENA_SCORE("[竞技场总战绩分享]"),
    SHARE_PLAYFIELD("[比赛分享]"),
    REPLY_SCORE("[战绩回放]"),
    RED_PACKET("[红包]"),
    MINI_GAME("[小游戏]"),
    DEL(null),
    WALLET_TRANSFER("[转账]"),
    GROUP_RED_PACKET("[群红包]"),
    GROUP_RED_MINE("[群红包埋雷]"),
    GIF("[GIF]"),
    GROUP_FINANCIA_UP("[财务上分]"),
    SEND_ARENA_VALUE("[赠送竞技值]"),
    ;

    private String alter;

    EChatContentType(String alter) {
        this.alter = alter;
    }

    public String getAlter() {
        return alter;
    }
}
