package com.xiuxiu.app.server.room.normal.poker.action;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.cow.PCLIPokerNtfCowSelectBankerInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.CowRoom;

import java.util.HashMap;
import java.util.Map;

public class SGLordBankerAction extends BasePokerAction {
    protected HashMap<Long, Integer> selectBanker = new HashMap<>();
    protected int selectCnt = 0;
    protected Long curSelectPlayerUid;
    protected Long ownPlayerUid;

    public SGLordBankerAction(PokerRoom room, long timeout) {
        super(room, EActionOp.ROB_BANKER, null, timeout);
    }

    public void addLordSelectBanker(long playerUid) {
        this.selectBanker.putIfAbsent(playerUid, -1);
    }

    public void setOwnPlayerUid(long uid) {
        this.ownPlayerUid = uid;
    }

    public Long getCurSelectPlayerUid() {
        return this.curSelectPlayerUid;
    }

    public ErrorCode startSelectBanker(long playerUid) {
        Integer temp = this.selectBanker.get(playerUid);
        if (null == temp) {
            Logs.ROOM.warn("%s 不在该房间内", playerUid);
            return ErrorCode.REQUEST_INVALID;
        }
        if (0 == temp) {
            Logs.ROOM.warn("%s 已经弃庄 selvalue:%d", playerUid, temp);
            return ErrorCode.ROOM_POKER_COW_ALREADY_ROB_BANKER;
        }

        curSelectPlayerUid = playerUid;
        if (!(selectCnt > 0 && playerUid == ownPlayerUid)) {
            PCLIPokerNtfCowSelectBankerInfo selectBankerInfo = new PCLIPokerNtfCowSelectBankerInfo();
            selectBankerInfo.selectPlayerUid = curSelectPlayerUid;
            this.room.broadcast2Client(CommandId.CLI_NTF_POKER_COW_LORD_BANKER_SELECT, selectBankerInfo);
        }
        ++this.selectCnt;

        return ErrorCode.OK;
    }

    public ErrorCode setSelectBanker(long playerUid, int selectState) {
        Integer temp = this.selectBanker.get(playerUid);
        if (null == temp) {
            Logs.ROOM.warn("%s 不在该房间内", playerUid);
            return ErrorCode.REQUEST_INVALID;
        }
        if (0 == temp) {
            Logs.ROOM.warn("%s 已经弃庄 selvalue:%d", playerUid, temp);
            return ErrorCode.ROOM_POKER_COW_ALREADY_ROB_BANKER;
        }

        this.selectBanker.put(playerUid, selectState);
        if (selectState == 0) {
            //弃庄 选择下一个；
          ((CowRoom) this.room).setNextLordBaner(playerUid);
        }
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
              //时间超时，选择当前未有操作的玩家为庄家；
            ((CowRoom) this.room).sendLordBankerResult(curSelectPlayerUid);
            return true;
        }

        long uid = 0;
        for (Map.Entry<Long, Integer> entry : this.selectBanker.entrySet()) {
            if (entry.getValue() == 1) {
                uid = entry.getKey();
                break;
            }
        }

        if (uid > 0 || this.selectCnt >= this.selectBanker.size()) {
            //如果是都没有选择庄那就选择房主为庄；
            ((CowRoom) this.room).sendLordBankerResult(uid);
            return true;
        }

        return false;
    }

    @Override
    public void online(IRoomPlayer player) {
        if(curSelectPlayerUid > 0){
            PCLIPokerNtfCowSelectBankerInfo selectBankerInfo = new PCLIPokerNtfCowSelectBankerInfo();
            selectBankerInfo.selectPlayerUid = curSelectPlayerUid;
            this.room.broadcast2Client(CommandId.CLI_NTF_POKER_COW_LORD_BANKER_SELECT, selectBankerInfo);
        }
    }

    @Override
    protected void doRecover() {
        if(curSelectPlayerUid > 0){
            PCLIPokerNtfCowSelectBankerInfo selectBankerInfo = new PCLIPokerNtfCowSelectBankerInfo();
            selectBankerInfo.selectPlayerUid = curSelectPlayerUid;
            this.room.broadcast2Client(CommandId.CLI_NTF_POKER_COW_LORD_BANKER_SELECT, selectBankerInfo);
        }
    }
}

