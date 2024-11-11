package com.xiuxiu.app.server.services.gateway.handler.poker.hundred;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfBankerRecord;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqBankerRecord;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.db.IDBLoad;
import com.xiuxiu.app.server.db.dao.IBaseDAO;
import com.xiuxiu.app.server.db.dao.IHundredBureauRecordDAO;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.Hundred.EHundredArenaRebType;
import com.xiuxiu.app.server.room.normal.Hundred.HundredBureauRecordInfo;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

import java.util.List;

public class HundredBankerRecordHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIHundredReqBankerRecord info = (PCLIHundredReqBankerRecord) request;
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ARENA.warn("%s 百人场不存在, 无法获取记录 info:%s", player, info);
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_BANKER_RECORD_FAIL, ErrorCode.ARENA_NOT_EXISTS);
            return null;
        }
        PCLIHundredNtfBankerRecord record = new PCLIHundredNtfBankerRecord();
        List<HundredBureauRecordInfo> list = DBManager.I.loadBatch(ETableType.TB_HUNDRED_BUREAU_RECORD, new IDBLoad<HundredBureauRecordInfo>() {
            @Override
            public String getRedisKey() {
                return ETableType.TB_HUNDRED_BUREAU_RECORD.getRedisKey();
            }

            @Override
            public List<HundredBureauRecordInfo> load(IBaseDAO<HundredBureauRecordInfo> dao) {
                return ((IHundredBureauRecordDAO) dao).loadBankerByRoomId(info.roomId, player.getUid(), info.page * Constant.PAGE_CNT_10, Constant.PAGE_CNT_10);
            }
        });
        if (list != null) {
            for (HundredBureauRecordInfo recordInfo : list) {
                //只显示最近30条
                if (info.page >= 3) {
                    break;
                }
                PCLIHundredNtfBankerRecord.ArenaBankerRecordInfo bankerRecord = new PCLIHundredNtfBankerRecord.ArenaBankerRecordInfo();
                bankerRecord.time = recordInfo.getTime();
                bankerRecord.value = recordInfo.getBankerWinValue()/ Constant.ARENA_VALUE_ODDS;
                //bankerRecord.bankerReb=new ArenaBankerRebInfo();
                for (int i = 0, len = recordInfo.getCardInfo().size(); i < len; ++i) {
                    HundredBureauRecordInfo.CardInfo temp = recordInfo.getCardInfo().get(i);
                    PCLIHundredNtfBankerRecord.ArenaBankerRebInfo cardInfo = new PCLIHundredNtfBankerRecord.ArenaBankerRebInfo();
                    cardInfo.cardType = temp.getCardType();
                    cardInfo.cards = temp.getCards();
                    cardInfo.winValue = temp.getValue()/Constant.ARENA_VALUE_ODDS;
                    Integer v = temp.getRebs().get(EHundredArenaRebType.PLAYER_WIN.ordinal());
                    cardInfo.value = v == null ? 0 : v/Constant.ARENA_VALUE_ODDS;
                    bankerRecord.rebs.put(i + 1, cardInfo);
                }
                record.records.add(bankerRecord);
            }
        }
        record.boxId = info.boxId;
        record.roomId = info.roomId;
        record.page = info.page;
        record.next = record.records.size() == Constant.PAGE_CNT_10;
        player.send(CommandId.CLI_NTF_ARENA_HUNDRED_BANKER_RECORD_OK, record);
        return null;
    }
}
