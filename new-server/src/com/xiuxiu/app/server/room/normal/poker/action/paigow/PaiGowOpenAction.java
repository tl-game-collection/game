package com.xiuxiu.app.server.room.normal.poker.action.paigow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowAllOpenInfo;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.BasePokerAction;
import com.xiuxiu.app.server.room.normal.poker.paigow.IPaiGowRoom;
import com.xiuxiu.app.server.room.player.poker.PaiGowPlayer;
import com.xiuxiu.app.server.room.record.poker.PaiGowOpenRecordAction;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaiGowOpenAction extends BasePokerAction {
    protected ConcurrentHashMap<Long, Boolean> allOpen = new ConcurrentHashMap<>();
    protected List<Long> allOpenList = new ArrayList<Long>();//已经开牌的玩家
    protected int selectCnt = 0;

    protected PaiGowOpenRecordAction action;

    /**
     * 构造函数
     * @param room
     * @param timeout
     */
    public PaiGowOpenAction(PokerRoom room, long timeout) {
        super(room, EActionOp.OPEN_CARD, null, timeout);
        this.action = ((PokerRecord) this.room.getRecord()).addPaiGowOpenRecordAction();
    }

    /**
     * 添加未操作开牌的玩家
     * @param uid
     */
    public void addOpenCard(Long uid){
        this.allOpen.put(uid, false);
    }

    /**
     * 执行开牌
     * @param playerUid
     * @param card1
     * @param card2
     * @return
     */
    public ErrorCode open(long playerUid, List<Byte> card1, List<Byte> card2){
        //已经开过牌
        if(this.allOpen.getOrDefault(playerUid,false)){
            return ErrorCode.REPEAT_OPERATE;
        }
        this.allOpen.put(playerUid, true);
        List<Byte> openCardList = new ArrayList<>();
        openCardList.addAll(card1);
        openCardList.addAll(card2);
        this.action.addOpenCard(playerUid, openCardList);
        ++selectCnt;
        this.allOpenList.add(playerUid);

        Iterator<Map.Entry<Long, Boolean>> it = this.allOpen.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Boolean> entry = it.next();
            long _playerUid = entry.getKey();
            PaiGowPlayer player = (PaiGowPlayer) this.room.getRoomPlayer(_playerUid);
            PCLIPokerNtfPaiGowAllOpenInfo info = new PCLIPokerNtfPaiGowAllOpenInfo();
            info.allOpenCardUids.addAll(this.allOpenList);
            info.openCards = player.getOpenCards();
            this.room.getRoomPlayer(entry.getKey()).send(CommandId.CLI_NTF_POKER_PAI_GOW_OPEN_CARD_INFO, info);
        }

        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if(timeout){
            for (Map.Entry<Long, Boolean> entry : this.allOpen.entrySet()) {
                PaiGowPlayer player = (PaiGowPlayer) this.room.getRoomPlayer(entry.getKey());
                //如果没开过牌
                if (null != player && !entry.getValue()) {
                    ++selectCnt;
                    this.action.addOpenCard(player.getUid(), player.getDefaultListCard());
                }
            }
        }

        if (this.selectCnt == this.allOpen.size()) {
            ((IPaiGowRoom)this.room).onOpenOver();//开牌结束
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        Iterator<Map.Entry<Long, Boolean>> it = this.allOpen.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Boolean> entry = it.next();
            long playerUid = entry.getKey();
            PaiGowPlayer player = (PaiGowPlayer) this.room.getRoomPlayer(playerUid);
            PCLIPokerNtfPaiGowAllOpenInfo info = new PCLIPokerNtfPaiGowAllOpenInfo();
            info.allOpenCardUids.addAll(this.allOpenList);
            info.openCards = player.getOpenCards();
            this.room.getRoomPlayer(entry.getKey()).send(CommandId.CLI_NTF_POKER_PAI_GOW_OPEN_CARD_INFO, info);
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        PaiGowPlayer pgPlayer = (PaiGowPlayer) this.room.getRoomPlayer(player.getUid());
        PCLIPokerNtfPaiGowAllOpenInfo info = new PCLIPokerNtfPaiGowAllOpenInfo();
        info.allOpenCardUids.addAll(this.allOpenList);
        info.openCards = pgPlayer.getOpenCards();
        player.send(CommandId.CLI_NTF_POKER_PAI_GOW_OPEN_CARD_INFO, info);
    }
}
