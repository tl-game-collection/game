package com.xiuxiu.app.server.services.gateway.handler.room;

import java.util.List;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.*;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.handle.IRoomHandle;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.score.BoxArenaScoreInfo;
import com.xiuxiu.core.net.message.Handler;

public class BoxArenaScoreRecordHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIArenaReqRecordInfo info = (PCLIArenaReqRecordInfo) request;
        if (!RoomManager.I.lock(player.getUid())) {
            Logs.ROOM.warn("%s 正在忙", player);
            player.send(CommandId.CLI_NTF_ARENA_SCORE_RECORD_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            if (player.getRoomId() == -1) {
                return null;
            }
            Room room = RoomManager.I.getRoom(player.getRoomId());
            if (null == room) {
                return null;
            }
            IRoomHandle roomHandle = room.getRoomHandle();
            if (!(roomHandle instanceof IBoxRoomHandle)) {
                return null;
            }
            IBoxRoomHandle boxRoomHandle = (IBoxRoomHandle)roomHandle;
            
            if(info.gameType== GameType.GAME_TYPE_COW){
                PCLIArenaNtfCowRecordtInfo recordInfo = new PCLIArenaNtfCowRecordtInfo();
                recordInfo.page=info.page;
                List<BoxArenaScoreInfo> list=DBManager.I.getBoxArenaScoreInfoDao().loadAllByBoxUid(player.getUid(),boxRoomHandle.getBoxUid(),info.page,4);
                for (BoxArenaScoreInfo boxArenaScoreInfo : list) {
                    recordInfo.list.add(boxArenaScoreInfo.toProtocolCowScoreInfo());
                }
                recordInfo.next=list.size()==4;
                player.send(CommandId.CLI_NTF_ARENA_SCORE_RECORD_OK, recordInfo);

            }else if(info.gameType== GameType.GAME_TYPE_FRIED_GOLDEN_FLOWER){
                PCLIArenaNtfFgfRecordtInfo recordInfo = new PCLIArenaNtfFgfRecordtInfo();
                recordInfo.page=info.page;
                List<BoxArenaScoreInfo> list=DBManager.I.getBoxArenaScoreInfoDao().loadAllByBoxUid(player.getUid(),boxRoomHandle.getBoxUid(),info.page,4);
                for (BoxArenaScoreInfo boxArenaScoreInfo : list) {
                    recordInfo.list.add(boxArenaScoreInfo.toProtocolFgfScoreInfo());
                }
                recordInfo.next=list.size()==4;
                player.send(CommandId.CLI_NTF_ARENA_SCORE_RECORD_OK, recordInfo);

            } else if (info.gameType == GameType.GAME_TYPE_PAIGOW) {
                PCLIArenaNtfPaiCowRecordtInfo recordInfo = new PCLIArenaNtfPaiCowRecordtInfo();
                recordInfo.page=info.page;
                List<BoxArenaScoreInfo> list=DBManager.I.getBoxArenaScoreInfoDao().loadAllByBoxUid(player.getUid(),boxRoomHandle.getBoxUid(),info.page,4);
                for (BoxArenaScoreInfo boxArenaScoreInfo : list) {
                    recordInfo.list.add(boxArenaScoreInfo.toProtocolPaiCowScoreInfo());
                }
                recordInfo.next=list.size()==4;
                player.send(CommandId.CLI_NTF_ARENA_SCORE_RECORD_OK, recordInfo);
            }else if(info.gameType== GameType.GAME_TYPE_SG){
                PCLIArenaNtfSGRecordInfo recordInfo = new PCLIArenaNtfSGRecordInfo();
                recordInfo.page=info.page;
                List<BoxArenaScoreInfo> list=DBManager.I.getBoxArenaScoreInfoDao().loadAllByBoxUid(player.getUid(),boxRoomHandle.getBoxUid(),info.page,4);
                for (BoxArenaScoreInfo boxArenaScoreInfo : list) {
                    recordInfo.list.add(boxArenaScoreInfo.toProtocolSGScoreInfo());
                }
                recordInfo.next=list.size()==4;
                player.send(CommandId.CLI_NTF_ARENA_SCORE_RECORD_OK, recordInfo);

            }else{
                PCLIArenaNtfThirteenRecordtInfo recordInfo = new PCLIArenaNtfThirteenRecordtInfo();
                recordInfo.page=info.page;
                List<BoxArenaScoreInfo> list=DBManager.I.getBoxArenaScoreInfoDao().loadAllByBoxUid(player.getUid(),boxRoomHandle.getBoxUid(),info.page,4);
                for (BoxArenaScoreInfo boxArenaScoreInfo : list) {
                    recordInfo.list.add(boxArenaScoreInfo.toProtocolThirteenScoreInfo());
                }
                recordInfo.next=list.size()==4;
                player.send(CommandId.CLI_NTF_ARENA_SCORE_RECORD_OK, recordInfo);
            }
            return null;
        } finally {
            RoomManager.I.unlock(player.getUid());
        }
    }
}
