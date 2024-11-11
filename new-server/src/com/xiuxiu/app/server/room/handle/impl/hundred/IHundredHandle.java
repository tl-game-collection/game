package com.xiuxiu.app.server.room.handle.impl.hundred;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.normal.Hundred.EHundredArenaRebType;
import com.xiuxiu.app.server.room.normal.Hundred.EHundredGameState;
import com.xiuxiu.app.server.room.normal.Hundred.HundredBureauRecordInfo;
import com.xiuxiu.app.server.room.normal.Hundred.HundredRebRecordInfo;
import com.xiuxiu.app.server.room.normal.Hundred.IHundredBanker;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.player.IHundredPlayer;

public interface IHundredHandle {

    ErrorCode upBanker(IPlayer player, HashMap<String, Integer> param);
    ErrorCode downBanker(IPlayer player, int bankerUid);
    ErrorCode reb(IPlayer player, int index, int value, EHundredArenaRebType type);
    ErrorCode vipSeatOp(long playerUid, int index);
    EHundredGameState getState();
    void setWinIndex(int index);
    int getWinIndex();
    IHundredBanker getCurBanker();
    CopyOnWriteArrayList<IHundredBanker> getBankerList();
    List<Integer> getWinIndexList();
    IHundredPlayer[] getAllPlayerList();
    IRoomPlayer getRoomPlayer(long playerUid);
    void broadcast(int commandId, Object msg);
    List<HundredRebRecordInfo> getRebRecordInfos(long playerUid);
    List<HundredBureauRecordInfo> getRBureauRecordInfos();
}
