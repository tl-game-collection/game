package com.xiuxiu.app.server.room.normal.poker.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfCallScoreInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.landLord.LandLordRoom;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.poker.PokerPlayer;
import com.xiuxiu.app.server.room.record.poker.LandLordCallScoreRecordAction;
import com.xiuxiu.core.KeyValue;

public class PokerCallScoreAction extends BasePokerAction  {
    protected int curScore = 0;//当前叫分
    protected int beginCallScoreIndex = -1;
    protected int curCallScoreIndex = -1;
    protected int maxScore = Integer.MIN_VALUE;
    protected int maxCallScoreIndex = -1;
    protected boolean isFinish = false;
    protected int playerNum = 0;
    protected static final int MAX_SCORE = 3;                             // 最大分
    protected long nextPlayerUid = -1;                                    // 下一个叫分玩家UID
    protected LandLordCallScoreRecordAction action;
    protected int loopingTimes;
    protected boolean forcedCall = false;

    public PokerCallScoreAction(PokerRoom room, PokerPlayer player, long timeout, LandLordCallScoreRecordAction action, int loopingTimes) {
        super(room, EActionOp.CALL_SCORE, player, timeout);
        this.action = action;
        this.loopingTimes = loopingTimes;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    public void setFirstCallScoreIndex(int curCallScoreIndex) {
        this.curCallScoreIndex = curCallScoreIndex;
        this.beginCallScoreIndex = curCallScoreIndex;
        this.resetTimeout(10000);
    }

    public ErrorCode callScore(long playerUid, int score) {
        IRoomPlayer player = this.room.getRoomPlayer(this.curCallScoreIndex);
        this.setCurScore(score);

        this.action.addCallScore(playerUid,score);

        if (player.getUid() != playerUid) {
            // error
            Logs.ROOM.warn("%s 当前轮叫分人是:%s 而不是你:%d 无效叫分", this, player, playerUid);
            return ErrorCode.REQUEST_INVALID;
        }
        if ((score != 0) && (score < this.maxScore)) {
            // invalid
            Logs.ROOM.warn("%s playerUid:%d 无效", this, player.getUid());
            return ErrorCode.REQUEST_INVALID_DATA;
        }

        if (this.maxScore < score) {
            this.maxScore = score;
            this.maxCallScoreIndex = this.curCallScoreIndex;
        }

        if (score == MAX_SCORE) {
            this.isFinish = true;
            this.nextPlayerUid = -1;
            Logs.ROOM.debug("--- 已经叫完");
        }

        if (!this.isFinish) {
            int nextCallerIndex = (this.curCallScoreIndex + 1) % this.playerNum;
            IRoomPlayer nextCaller = this.room.getRoomPlayer(nextCallerIndex);
            while (nextCaller == null || nextCaller.isGuest()) {
                nextCallerIndex = (nextCallerIndex + 1) % this.playerNum;
                nextCaller = this.room.getRoomPlayer(nextCallerIndex);
            }

            if (nextCallerIndex == this.beginCallScoreIndex) {
                this.curCallScoreIndex = nextCallerIndex;
                this.nextPlayerUid = -1;
                if (this.maxScore <= 0) {
                    Logs.ROOM.debug("--- 叫分结束，都不叫，重新发牌 %d", this.loopingTimes);
                } else {
                    Logs.ROOM.debug("--- 叫分结束，最终叫分: %d", this.maxScore);
                }
            } else {
                this.curCallScoreIndex = nextCallerIndex;
                this.nextPlayerUid = nextCaller.getUid();

                // 第3轮叫分，如果前面的玩家都不叫，则最后一名玩家被强制叫分
                if (this.loopingTimes >= 2 && this.maxScore <= 0) {
                    nextCallerIndex = (nextCallerIndex + 1) % this.playerNum;
                    nextCaller = this.room.getRoomPlayer(nextCallerIndex);
                    while (nextCaller == null || nextCaller.isGuest()) {
                        nextCallerIndex = (nextCallerIndex + 1) % this.playerNum;
                        nextCaller = this.room.getRoomPlayer(nextCallerIndex);
                    }
                    if (nextCaller.getIndex() == this.beginCallScoreIndex) {
                        this.forcedCall = true;
                    }
                }

                if (!this.forcedCall) {
                    Logs.ROOM.debug("--- 下一个叫分玩家：%d", this.nextPlayerUid);
                } else {
                    Logs.ROOM.debug("--- 强制叫分玩家：%d", this.nextPlayerUid);
                }
            }
        }

        PCLIPokerNtfCallScoreInfo info = new PCLIPokerNtfCallScoreInfo();
        info.callPlayerUid = playerUid;
        info.score = score;
        info.maxScore = this.maxScore;
        info.nextCallPlayerUid = this.forcedCall ? -1 : this.nextPlayerUid;
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_LAND_LORD_CALL_SCORE, info);
        this.resetTimeout(this.forcedCall ? 100 : 10000);

        return ErrorCode.OK;
    }

    public int getCurScore() {
        return curScore;
    }

    public void setCurScore(int score) {
        this.curScore = score;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            Logs.ROOM.debug("--- timeout, facedCall:%s", this.forcedCall);
            IRoomPlayer player = this.room.getRoomPlayer(this.curCallScoreIndex);
            this.callScore(player.getUid(), this.forcedCall ? 1 : 0);
        }
        if (this.isFinish) {
            // 开始发牌 maxCallScoreIndex
            Logs.ROOM.debug("--- isFinish, to call onLastCard");
            IRoomPlayer player = this.room.getRoomPlayer(this.maxCallScoreIndex);
            ((LandLordRoom)this.room).onLastCard((IPokerPlayer) player, this.maxScore, this.action.getAllCallScore());
            return true;
        }
        if (((this.curCallScoreIndex) % this.playerNum) == this.beginCallScoreIndex) {
            if (this.maxScore < 1) {
                // 重新洗牌叫牌
                ((LandLordRoom)this.room).onDealCard();
            } else {
                // 开始发牌 maxCallScoreIndex
                IRoomPlayer player = this.room.getRoomPlayer(this.maxCallScoreIndex);
                ((LandLordRoom)this.room).onLastCard((IPokerPlayer) player, this.maxScore, this.action.getAllCallScore());
            }
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        IRoomPlayer curPlayer = this.room.getRoomPlayer(this.curCallScoreIndex);
        PCLIPokerNtfCallScoreInfo info = new PCLIPokerNtfCallScoreInfo();
        info.callPlayerUid = curPlayer.getUid();
        info.score = this.getCurScore();
        info.maxScore = this.maxScore;
        info.nextCallPlayerUid = this.nextPlayerUid;
        this.player.send(CommandId.CLI_NTF_POKER_LAND_LORD_CALL_SCORE, info);
    }

    @Override
    public void online(IRoomPlayer player) {
        IRoomPlayer curPlayer = this.room.getRoomPlayer(this.curCallScoreIndex);
        if (curPlayer.getUid() != player.getUid()) {
            Logs.ROOM.warn("%s 当前轮叫分人是:%s 而不是你:%s 无效叫分", this, curPlayer, player);
            return;
        }
        PCLIPokerNtfCallScoreInfo info = new PCLIPokerNtfCallScoreInfo();
        info.callPlayerUid = curPlayer.getUid();
        info.score = this.getCurScore();
        info.maxScore = this.maxScore;
        info.nextCallPlayerUid = this.nextPlayerUid;
        player.send(CommandId.CLI_NTF_POKER_LAND_LORD_CALL_SCORE, info);
    }
}
