package com.xiuxiu.app.server.room.normal.poker.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPlayerPrimulaInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.runFast.RunFastRoom;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;
import com.xiuxiu.app.server.room.record.poker.RunFastBeginPrimulaRecordAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class RunFastBeginPrimulaAction extends BasePokerAction {
    private HashMap<Long, Integer> allPrimula = new HashMap<>();
    private int selectCnt = 0;

    protected RunFastBeginPrimulaRecordAction action;

    public RunFastBeginPrimulaAction(PokerRoom room, long timeout) {
        super(room, EActionOp.PRIMULA, null, timeout);
        action = ((PokerRecord) this.room.getRecord()).addRunFastBeginPrimulaRecordAction();
    }

    public void addPrimula(long playerUid) {
        this.allPrimula.putIfAbsent(playerUid, -1);
    }

    public HashMap<Long, Integer> getAllPrimula() {
        return allPrimula;
    }

    public void setAllPrimula(HashMap<Long, Integer> allPrimula) {
        this.allPrimula = allPrimula;
    }

    public ErrorCode selectPrimula(long playerUid, int primula) {
        Integer temp = this.allPrimula.get(playerUid);
        if (null == temp) {
            Logs.ROOM.warn("%s 不在该房间内", playerUid);
            return ErrorCode.REQUEST_INVALID;
        }
        if (-1 != temp) {
            Logs.ROOM.warn("%s 已经叫过了 ", playerUid);
            return ErrorCode.REQUEST_INVALID;
        }
        this.allPrimula.put(playerUid, primula);
        ++this.selectCnt;

        PCLIPokerNtfPlayerPrimulaInfo info = new PCLIPokerNtfPlayerPrimulaInfo();
        info.playerUid = playerUid;
        info.isPrimula = primula;
        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_PLAYER_PRIMULA_INFO, info);
        this.action.addAllPrimula(playerUid, primula);
        return ErrorCode.OK;
    }

    @Override
    protected void doRecover() {

    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            for (Map.Entry<Long, Integer> entry : this.allPrimula.entrySet()) {
                if (-1 == entry.getValue()) {
                    IPokerPlayer player = (IPokerPlayer) this.room.getRoomPlayer(entry.getKey());
                    if (null != player) {
                        PCLIPokerNtfPlayerPrimulaInfo info = new PCLIPokerNtfPlayerPrimulaInfo();
                        info.playerUid = player.getUid();
                        info.isPrimula = 0;
                        this.room.broadcast2Client(CommandId.CLI_NTF_POKER_PLAYER_PRIMULA_INFO, info);
                        this.action.addAllPrimula(player.getUid(), 0);
                    }
                    ++this.selectCnt;
                }
            }
        }

        if (this.selectCnt == this.allPrimula.size()) {
            List<Long> maxPlayer = new ArrayList<>();
            int index = 0;
            for (Map.Entry<Long, Integer> entry : this.allPrimula.entrySet()) {
                if (0 < entry.getValue()) {
                    maxPlayer.add(entry.getKey());
                    index++;
                }
            }
            ((RunFastRoom) this.room).setMaxPrimula(maxPlayer, index);
            return true;
        }
        return false;
    }

    @Override
    public void online(IRoomPlayer player) {
        for (Map.Entry<Long, Integer> entry : this.allPrimula.entrySet()) {
            PCLIPokerNtfPlayerPrimulaInfo info = new PCLIPokerNtfPlayerPrimulaInfo();
            info.playerUid = entry.getKey();
            info.isPrimula = entry.getValue();
            player.send(CommandId.CLI_NTF_MAHJONG_SELECT_PIAO_INFO, info);
        }
    }
}
