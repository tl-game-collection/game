package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfCSHuInfo;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongStartHu;
import com.xiuxiu.app.server.room.normal.mahjong2.bird.IMahjongBird;
import com.xiuxiu.app.server.room.player.mahjong2.ICSMJMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.record.mahjong2.CSStartHuRecordAction;
import com.xiuxiu.app.server.room.record.mahjong2.MahjongRecord;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.ArrayList;
import java.util.List;

public class MahjongCSStartHuAction extends BaseMahjongAction {
    private ArrayList<ICSMJMahjongPlayer> allHuPlayer = new ArrayList<>();
    private ArrayList<List<Byte>> allHuPlayerWithBright = new ArrayList<>();
    private boolean finish = false;
    private int curIndex = 0;
    private CSStartHuRecordAction csStartHuRecordAction;

    private IMahjongBird bird;
    private int niaoScore = 1;
    private boolean bankerInc = false;

    private long expire = -1;

    public MahjongCSStartHuAction(IRoom room) {
        super(room, EActionOp.CS_START_HU, -1);
    }

    public void setNiaoInfo(boolean bankerInc, int niaoType, int niaoNum, int niaoScore) {
        this.bankerInc = bankerInc;
        this.bird = IMahjongBird.get(niaoType);
        this.niaoScore = niaoScore;
        if (null != bird) {
            bird.setCnt(niaoNum);
        }
    }

    public void addPlayer(ICSMJMahjongPlayer player, List<Byte> bright) {
        this.allHuPlayer.add(player);
        this.allHuPlayerWithBright.add(bright);
    }

    public void start() {
        this.csStartHuRecordAction = ((MahjongRecord) this.room.getRecord()).addCSStartHuAction();
        this.expire = System.currentTimeMillis() + 6000;
    }

    public void next() {
        ICSMJMahjongPlayer player = this.allHuPlayer.get(this.curIndex);

        PCLIMahjongNtfCSHuInfo info = new PCLIMahjongNtfCSHuInfo();
        info.huPlayerUid = player.getUid();
        player.addStartHuPaiXingTo(info.allPx);
        info.handCard.addAll(this.allHuPlayerWithBright.get(this.curIndex));
        ++this.curIndex;

        if (null != this.bird) {
            for (int i = 0, len = this.bird.getCnt(); i < len; ++i) {
                byte niao = (byte) RandomUtil.random(1, 6);
                this.bird.isHit((IMahjongRoom) this.room, player, niao);
                info.capList.add(niao);
            }
            int totalValue = 0;
            for (int i = 0, len = this.room.getMaxPlayerCnt(); i < len; ++i) {
                IMahjongPlayer otherPlayer = (IMahjongPlayer) this.room.getRoomPlayer(i);
                if (otherPlayer==null||null == player || player.isGuest() || player.getUid() == otherPlayer.getUid()) {
                    continue;
                }
                int niaoScore = this.niaoScore;
                if (0 == this.niaoScore) {
                    niaoScore = 1;
                }
                int value = this.bird.calcNiaoScore(player, otherPlayer, niaoScore);
                if (0 == this.niaoScore) {
                    value = (int) (info.allPx.size() * 2 * Math.pow(2, value));
                } else {
                    value = info.allPx.size() * 2 + value;
                }
                if (this.bankerInc && (this.room.getBankerIndex() == player.getIndex() || this.room.getBankerIndex() == otherPlayer.getIndex())) {
                    ++value;
                }
                totalValue += value;
                otherPlayer.addScore(Score.MJ_CUR_START_HU_SCORE, -value, false);
                player.addScore(Score.MJ_CUR_START_HU_SCORE, value, false);
                info.allScore.put(otherPlayer.getUid(), -value);
            }
            info.allScore.put(player.getUid(), totalValue);
            this.bird.clear((IMahjongRoom) this.room);
        }
        this.csStartHuRecordAction.addStartHu(player.getUid(), info.handCard, info.allPx, info.capList, info.allScore);

        this.room.broadcast2Client(CommandId.CLI_NTF_MAHJONG_START_HU_INFO, info);

        this.expire = System.currentTimeMillis() + 6000 + 1000 * info.allPx.size();
        if (this.curIndex >= this.allHuPlayer.size()) {
            this.finish = true;
        }
    }

    @Override
    public boolean action(boolean timeout) {
        if (!this.finish) {
            if (timeout) {
                this.next();
            }
            return false;
        }
        ((IMahjongStartHu) this.room).doSendEndStartHu();
        ((IMahjongStartHu) this.room).endStartHu(false);
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
