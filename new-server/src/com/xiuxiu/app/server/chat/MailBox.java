package com.xiuxiu.app.server.chat;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.protocol.client.chat.PCLIChatMsg;
import com.xiuxiu.app.server.constant.SystemTipMessageConstant;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.List;

public class MailBox extends BaseTable {
    protected long messageUid;          // 消息UID
    protected long messageUidByPlayer;  // 相对于玩家消息uid
    protected long toPlayerUid;         // 接收玩家UID
    protected long tagPlayerUid = -1;   // 目标用户uid
    protected long fromPlayerUid = -1;  // 来自玩家UID
    protected String fromPlayerName = "";// 来自玩家名字
    protected long fromGroupUid = -1;   // 来自群UID
    protected long toGroupUid = -1;     // 发给clubUid;
    protected long fromLeagueUid = -1;   // 来自联盟UID
    protected long fromPavilionUid = -1; // 来自雀友圈UID
    protected byte messageType;         // 消息类型, 0:系统, 1:玩家 2:群, 3: 房间
    protected byte contentType;         // 内容类型, 0:文字, 1:图片, 2:语音, 3: 开启房间, 4; 包厢总战绩分享, 5: 提示, 6: 开启竞技场, 7: 好友名片, 8: 群名片, 9: 开启包间, 10, 房间分享, 11: 房间总战绩分享, 12: 竞技场总战绩分享, 13: 比赛分享, 14: 战绩回放, 15: 红包, 16: 小游戏, 17: 删除
    protected String message = "";      // 消息内容, 如果是语音bytes需要Base64
    protected long sayTime;             // 消息时间毫秒
    protected List<Object> param;       // 参数
    protected byte state = 0;           // 状态, 0: 正常, 1: 已经消费, 2: 更新, 3: 撤回, 4: 删除

    public MailBox() {
        this.tableType = ETableType.TB_MAILBOX;
    }

    public long getFromPavilionUid() {
        return fromPavilionUid;
    }

    public void setFromPavilionUid(long fromPavilionUid) {
        this.fromPavilionUid = fromPavilionUid;
    }

    public long getFromLeagueUid() {
        return fromLeagueUid;
    }

    public void setFromLeagueUid(long fromLeagueUid) {
        this.fromLeagueUid = fromLeagueUid;
    }
    public long getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(long messageUid) {
        this.messageUid = messageUid;
    }

    public long getMessageUidByPlayer() {
        return messageUidByPlayer;
    }

    public void setMessageUidByPlayer(long messageUidByPlayer) {
        this.messageUidByPlayer = messageUidByPlayer;
    }

    public long getToPlayerUid() {
        return toPlayerUid;
    }

    public void setToPlayerUid(long toPlayerUid) {
        this.toPlayerUid = toPlayerUid;
    }

    public long getTagPlayerUid() {
        return tagPlayerUid;
    }

    public void setTagPlayerUid(long tagPlayerUid) {
        this.tagPlayerUid = tagPlayerUid;
    }

    public long getFromPlayerUid() {
        return fromPlayerUid;
    }

    public void setFromPlayerUid(long fromPlayerUid) {
        this.fromPlayerUid = fromPlayerUid;
    }

    public String getFromPlayerName() {
        return fromPlayerName;
    }

    public void setFromPlayerName(String fromPlayerName) {
        this.fromPlayerName = fromPlayerName;
    }

    public long getFromGroupUid() {
        return fromGroupUid;
    }

    public void setFromGroupUid(long fromGroupUid) {
        this.fromGroupUid = fromGroupUid;
    }

    public long getToGroupUid() {
        return toGroupUid;
    }

    public void setToGroupUid(long toGroupUid) {
        this.toGroupUid = toGroupUid;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public byte getContentType() {
        return contentType;
    }

    public void setContentType(byte contentType) {
        this.contentType = contentType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSayTime() {
        return sayTime;
    }

    public void setSayTime(long sayTime) {
        this.sayTime = sayTime;
    }

    public List<Object> getParam() {
        return param;
    }

    public void setParam(List<Object> param) {
        this.param = param;
    }

    public String getParamDb() {
        if (null == this.param) {
            return null;
        }
        return JsonUtil.toJson(this.param);
    }

    public void setParamDb(String value) {
        if (!StringUtil.isEmptyOrNull(value)) {
            this.param = JsonUtil.fromJson(value, new TypeReference<List<Object>>() {});
        }
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public PCLIChatMsg to() {
        PCLIChatMsg chatMsg = new PCLIChatMsg();
        chatMsg.messageUid = this.messageUidByPlayer;
        chatMsg.sayTime = this.sayTime;
        chatMsg.messageType = this.messageType;
        if (4 == this.state) {
            chatMsg.message = String.format(SystemTipMessageConstant.TIP_DEL, null == this.param || this.param.isEmpty() ? "" : this.param.get(this.param.size() - 1));
            chatMsg.contentType = EChatContentType.DEL.ordinal();
        } else {
            chatMsg.message = this.message;
            chatMsg.contentType = this.contentType;
        }
        chatMsg.tagPlayerUid = this.tagPlayerUid;
        chatMsg.fromPlayerUid = this.fromPlayerUid;
        Player fromPlayer = PlayerManager.I.getPlayer(this.fromPlayerUid);
        chatMsg.fromPlayerName = null == fromPlayer ? "" : fromPlayer.getName();
        chatMsg.fromPlayerIcon = null == fromPlayer ? "" : fromPlayer.getIcon();
        chatMsg.fromGroupUid = this.fromGroupUid;
        chatMsg.toGroupUid = this.toGroupUid;
        chatMsg.fromLeagueUid = this.fromLeagueUid;
        chatMsg.fromPavilionUid = this.fromPavilionUid;
        chatMsg.param = this.param;
        return chatMsg;
    }

    @Override
    public String toString() {
        return "MailBox{" +
                "messageUid=" + messageUid +
                ", messageUidByPlayer=" + messageUidByPlayer +
                ", toPlayerUid=" + toPlayerUid +
                ", tagPlayerUid=" + tagPlayerUid +
                ", fromPlayerUid=" + fromPlayerUid +
                ", fromGroupUid=" + fromGroupUid +
                ", fromLeagueUid=" + fromLeagueUid +
                ", fromPavilionUid=" + fromPavilionUid +
                ", messageType=" + messageType +
                ", contentType=" + contentType +
                ", message='" + message + '\'' +
                ", sayTime=" + sayTime +
                ", param=" + param +
                ", state=" + state +
                ", isNew=" + isNew +
                ", tableType=" + tableType +
                ", uid=" + uid +
                ", dirty=" + dirty +
                '}';
    }
}
