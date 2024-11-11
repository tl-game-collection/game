package com.xiuxiu.app.protocol.client.chat;

import java.util.List;

public class PCLIChatMsg {
    public long messageUid;         // 消息UID
    //public long messageUidByPlayer; // 玩家消息UID
    public long tagPlayerUid;       // 目标用户uid
    public long fromPlayerUid;      // 来自玩家UID
    public String fromPlayerName;   // 来自玩家Name
    public String fromPlayerIcon;   // 来自玩家Icon
    public byte fromPlayerSex;      // 来自玩家性别(仅限于房间聊天)
    public long fromGroupUid;       // 来自群UID
    public long toGroupUid;         // 发给群UID
    public long fromLeagueUid;       // 来自联盟UID
    public long fromPavilionUid;    // 来自雀友圈UID
    public long messageType;        // 消息类型, 0:系统, 1:玩家 2:群 3:雀友圈
    public int contentType;         // 内容类型, 0:文字, 1:图片, 2:语音, 3: 开启房间, 4; 包厢总战绩分享, 5: 提示, 6: 开启竞技场, 7: 好友名片, 8: 群名片, 9: 开启包间, 10, 房间分享, 11: 房间总战绩分享, 12: 竞技场总战绩分享, 13: 比赛分享, 14: 战绩回放, 15: 红包, 16: 小游戏, 17: 删除, 18: 转账
    public String message;          // 消息内容, 如果是语音bytes需要Base64
    public long sayTime;            // 消息时间毫秒
    public List<Object> param;      // 参数

    @Override
    public String toString() {
        return "PCLIChatMsg{" +
                "messageUid=" + messageUid +
                ", tagPlayerUid=" + tagPlayerUid +
                ", fromPlayerUid=" + fromPlayerUid +
                ", fromPlayerName='" + fromPlayerName + '\'' +
                ", fromPlayerIcon='" + fromPlayerIcon + '\'' +
                ", fromPlayerSex=" + fromPlayerSex +
                ", fromGroupUid=" + fromGroupUid +
                ", toGroupUid=" + toGroupUid +
                ", fromLeagueUid=" + fromLeagueUid +
                ", fromPavilionUid=" + fromPavilionUid +
                ", messageType=" + messageType +
                ", contentType=" + contentType +
                ", message='" + message + '\'' +
                ", sayTime=" + sayTime +
                ", param=" + param +
                '}';
    }
}
