package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfCSHuInfo;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.BaseMahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongMiddleHu;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong2.bird.IMahjongBird;
import com.xiuxiu.app.server.room.player.mahjong2.ICSMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.record.mahjong2.CSStartHuRecordAction;
import com.xiuxiu.app.server.room.record.mahjong2.MahjongRecord;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.List;

public class MahjongCSMiddleHuAction extends BaseMahjongAction {
    private boolean finish = false;
    private CSStartHuRecordAction csStartHuRecordAction;

    private IMahjongBird bird;
    private int niaoScore = 1;
    private boolean bankerInc = false;

    private List<Byte> bright;
    private long expire;

    public MahjongCSMiddleHuAction(IRoom room, IMahjongPlayer player) {
        super(room, player, EActionOp.CS_MIDDLE_HU, BaseMahjongRoom.START_HU_TIMEOUT);
    }

    public void setNiaoInfo(boolean bankerInc, int niaoType, int niaoNum, int niaoScore) {
        this.bankerInc = bankerInc;
        this.bird = IMahjongBird.get(niaoType);
        this.niaoScore = niaoScore;
        if (null != bird) {
            bird.setCnt(niaoNum);
        }
    }

    public void setBright(List<Byte> bright) {
        this.bright = bright;
    }

    public void start() {
        this.csStartHuRecordAction = ((MahjongRecord) this.room.getRecord()).addCSStartHuAction();

        PCLIMahjongNtfCSHuInfo info = new PCLIMahjongNtfCSHuInfo();
        info.huPlayerUid = this.player.getUid();
        ((ICSMJMahjongPlayer) this.player).addMiddleHuPaiXingTo(info.allPx);
        info.handCard.addAll(this.bright);

        if (null != this.bird) {
            for (int i = 0, len = this.bird.getCnt(); i < len; ++i) {
                byte niao = (byte) RandomUtil.random(1, 6);
                this.bird.isHit((IMahjongRoom) this.room, this.player, niao);
                info.capList.add(niao);
            }
            int totalValue = 0;
            for (int i = 0, len = this.room.getCurPlayerCnt(); i < len; ++i) {
                IMahjongPlayer otherPlayer = (IMahjongPlayer) this.room.getRoomPlayer(i);
                if (otherPlayer==null||null == this.player || this.player.isGuest() || this.player.getUid() == otherPlayer.getUid()) {
                    continue;
                }
                int niaoScore = this.niaoScore;
                if (0 == this.niaoScore) {
                    niaoScore = 1;
                }
                int value = this.bird.calcNiaoScore(this.player, otherPlayer, niaoScore);
                if (this.bankerInc && (this.room.getBankerIndex() == this.player.getIndex() || this.room.getBankerIndex() == otherPlayer.getIndex())) {
                    ++value;
                }
                if (0 == this.niaoScore) {
                    value = (int) (info.allPx.size() * 2 * Math.pow(2, value));
                } else {
                    value = info.allPx.size() * 2 + value;
                }
                totalValue += value;
                otherPlayer.addScore(Score.MJ_CUR_START_HU_SCORE, -value, false);
                this.player.addScore(Score.MJ_CUR_START_HU_SCORE, value, false);
                info.allScore.put(otherPlayer.getUid(), -value);
            }
            info.allScore.put(this.player.getUid(), totalValue);
            this.bird.clear((IMahjongRoom) this.room);
        }
        this.csStartHuRecordAction.addStartHu(this.player.getUid(), info.handCard, info.allPx, info.capList, info.allScore);

        ((IMahjongMiddleHu) this.room).doSendBeginMiddleHu(info);

        this.finish = true;
        this.expire = System.currentTimeMillis() + 6000 + 1000 * info.allPx.size();
    }

    @Override
    public boolean action(boolean timeout) {
        if (!this.finish) {
            return false;
        }
        ((IMahjongMiddleHu) this.room).endMiddleHu(this.player);
        return true;
    }

    @Override
    public boolean canAction(long curTime) {
        //if (this.finish) {
        //    return true;
        //}
        return curTime >= this.expire;
    }


    @Override
    protected void doRecover() {
        //for (Map.Entry<Long, Integer> entry : this.allPlayer.entrySet()) {
        //    ((IMahjongStartHu) this.room).doSendBeginStartHu((IMahjongPlayer) this.room.getRoomPlayer(entry.getKey()));
        //}
    }

    @Override
    public void online(IRoomPlayer player) {
        //if (this.allPlayer.containsKey(player.getUid())) {
        //    ((IMahjongStartHu) this.room).doSendBeginStartHu((IMahjongPlayer) player);
        //}
    }
}
