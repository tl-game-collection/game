package com.xiuxiu.app.server.room.normal.poker.action.paigow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowAllRebetInfo;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowRebetInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.BasePokerAction;
import com.xiuxiu.app.server.room.normal.poker.paigow.EPaiGowSpecialType;
import com.xiuxiu.app.server.room.normal.poker.paigow.IPaiGowRoom;
import com.xiuxiu.app.server.room.normal.poker.paigow.PaiGowHotRoom;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.record.poker.PaiGowRebetRecordAction;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaiGowRebetAction extends BasePokerAction {
    protected ConcurrentHashMap<Long, Long> allReb = new ConcurrentHashMap<>();
    protected int base = 1;//底分
    protected int cnt = 0;

    protected PaiGowRebetRecordAction action;

    /**
     * 构造函数
     * @param room
     * @param timeout
     */
    public PaiGowRebetAction(PokerRoom room, long timeout) {
        super(room, EActionOp.REBET, null, timeout);
        this.action = ((PokerRecord) this.room.getRecord()).addPaiGowRebetRecordAction();
    }

    /**
     * 添加可下注玩家
     * @param playerUid
     */
    public void addCanPushNotePlayer(long playerUid) {
        this.allReb.put(playerUid, 0L);
    }

    /**
     * 设置底分
     * @param base
     */
    public void setBase(int base) {
        this.base = base;
    }

    /**
     * 执行下注
     * @param playerUid
     * @param rebetMap
     * @return
     */
    public ErrorCode rebet(long playerUid, List<Integer> rebetMap) {
        for (int i = 0; i < rebetMap.size(); i++) {
            if (rebetMap.get(i) == null) {
                rebetMap.set(i, 0);
            }
        }
        long rebValue = this.allReb.get(playerUid);
        if (0 != rebValue) {
            Logs.ROOM.warn("%s 已经下过注了", playerUid);
            return ErrorCode.ROOM_POKER_COW_ALREADY_REBET;
        }
        IRoomPlayer roomPlayer = this.room.getRoomPlayer(playerUid);
        for (int i = 0, len = rebetMap.size(); i < len; ++i) {
            rebValue |= (rebetMap.get(i) << (i * 11));
            if (0 == i) {
                roomPlayer.setScore(Score.POKER_PAIGOW_ONE_REB, rebetMap.get(i), false);//第1道下注
            } else if (1 == i) {
                roomPlayer.setScore(Score.POKER_PAIGOW_TWO_REB, rebetMap.get(i), false);//第2道下注
            } else if (2 == i) {
                roomPlayer.setScore(Score.POKER_PAIGOW_THREE_REB, rebetMap.get(i), false);//第3道下注
            }
            this.action.addRebet(roomPlayer.getUid(), 1 + i, rebetMap.get(i));
        }
        ++this.cnt;
        this.allReb.put(playerUid, rebValue);
        PCLIPokerNtfPaiGowRebetInfo info = new PCLIPokerNtfPaiGowRebetInfo();
        info.playerUid = playerUid;
        info.rebets = rebetMap;
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_PAI_GOW_REBET, info);//通知下注数据
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            for (Map.Entry<Long, Long> entry : this.allReb.entrySet()) {
                IPokerPlayer player = (IPokerPlayer) this.room.getRoomPlayer(entry.getKey());
                if (0 == entry.getValue()) {
                    int paiGowPlayMethod = this.room.getRule().getOrDefault(RoomRule.RR_PAIGOW_PLAY, 0);
                    if (room instanceof PaiGowHotRoom) {
                        PaiGowHotRoom paiGowHotRoom = (PaiGowHotRoom) this.room;
                        // 头把下满
                        if (paiGowHotRoom.getCurLoop() == 1 && paiGowPlayMethod > 0
                                && 0 != (paiGowPlayMethod & EPaiGowSpecialType.BANKER_FIRST_ALLIN.getValue())) {
                            IClub club = ClubManager.I.getClubByUid(this.room.getGroupUid());
                            long enterClubUid = club.getEnterFromClubUid(player.getUid());
                            if (enterClubUid != room.getGroupUid()) {
                                IClub tempClub = ClubManager.I.getClubByUid(enterClubUid);
                                if (tempClub != null) {
                                    club = tempClub;
                                }
                            }
                            int value = (int)club.getGold(player.getUid()) + player.getScore(Score.SCORE, false);
                            int curHotDeskNote = ((PaiGowHotRoom) this.room).curHotDeskNote;
                            int dao = this.room.getRule().getOrDefault(RoomRule.RR_PAIGOW_DAO_RULE, 2);
                            int rebetValue = curHotDeskNote / dao;
                            int extValue = curHotDeskNote % dao;
                            for (int i = 0; i < dao; i++) {
                                int tmValue = value - rebetValue;
                                if (tmValue < 0) {
                                    tmValue = value;
                                    value = 0;
                                } else {
                                    tmValue = rebetValue;
                                    value -= rebetValue;
                                }
                                if (i == 0) {
                                    if (extValue > 0)
                                        tmValue += extValue;
                                    player.setScore(Score.POKER_PAIGOW_ONE_REB, tmValue, false);
                                }
                                if (i == 1)
                                    player.setScore(Score.POKER_PAIGOW_TWO_REB, tmValue, false);
                                if (i == 2)
                                    player.setScore(Score.POKER_PAIGOW_THREE_REB, tmValue, false);
                                if (value <= 0)
                                    break;
                            }
                            continue;
                        }
                    }
                    player.setScore(Score.POKER_PAIGOW_ONE_REB, ((IPaiGowRoom) this.room).getDefaultBet(player), false);
                    player.setScore(Score.POKER_PAIGOW_TWO_REB, 0, false);
                    player.setScore(Score.POKER_PAIGOW_THREE_REB, 0, false);
                }
            }
            this.cnt = this.allReb.size();
        }

        if (this.cnt == this.allReb.size()) {
            ((IPaiGowRoom) this.room).onRebOver();
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        Iterator<Map.Entry<Long, Long>> it = this.allReb.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Long> entry = it.next();
            PCLIPokerNtfPaiGowAllRebetInfo info = new PCLIPokerNtfPaiGowAllRebetInfo();
            info.base = this.base;
            info.oneReb = (int) (entry.getValue() & 0x111FF);
            info.twoReb = (int) ((entry.getValue() >> 11) & 0x111FF);
            info.threeReb = (int) ((entry.getValue() >> 22) & 0x111FF);
            info.remain = this.getRemain();
            this.room.getRoomPlayer(entry.getKey()).send(CommandId.CLI_NTF_POKER_PAI_GOW_REBET_INFO, info);
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        if (!this.allReb.containsKey(player.getUid())) {
            return;
        }
        long reb = this.allReb.get(player.getUid());
        PCLIPokerNtfPaiGowAllRebetInfo info = new PCLIPokerNtfPaiGowAllRebetInfo();
        info.base = this.base;
        info.oneReb = (int) (reb & 0x111FF);
        info.twoReb = (int) ((reb >> 11) & 0x111FF);
        info.threeReb = (int) ((reb >> 22) & 0x111FF);
        info.remain = this.getRemain();
        player.send(CommandId.CLI_NTF_POKER_PAI_GOW_REBET_INFO, info);
    }
}
