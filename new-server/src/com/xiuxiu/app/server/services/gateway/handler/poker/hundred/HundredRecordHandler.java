package com.xiuxiu.app.server.services.gateway.handler.poker.hundred;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfRecord;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqRecord;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.db.IDBLoad;
import com.xiuxiu.app.server.db.dao.IBaseDAO;
import com.xiuxiu.app.server.db.dao.IHundredBureauRecordDAO;
import com.xiuxiu.app.server.db.dao.IHundredRebRecordDAO;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.impl.hundred.IHundredHandle;
import com.xiuxiu.app.server.room.normal.Hundred.EHundredArenaRebType;
import com.xiuxiu.app.server.room.normal.Hundred.HundredBureauRecordInfo;
import com.xiuxiu.app.server.room.normal.Hundred.HundredRebRecordInfo;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.NumberUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 请求获取战绩信息
 */
public class HundredRecordHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIHundredReqRecord info = (PCLIHundredReqRecord) request;
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ARENA.warn("%s 百人场不存在, 无法获取记录 info:%s", player, info);
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_RECORD_FAIL, ErrorCode.ARENA_NOT_EXISTS);
            return null;
        }
        IHundredHandle hundredHandle = (IHundredHandle)room.getRoomHandle();
        PCLIHundredNtfRecord record = new PCLIHundredNtfRecord();
        record.boxId = info.boxId;
        record.roomId = info.roomId;
        record.reb = info.reb;// false:房间记录
        record.page = info.page;
        record.pageSize = info.pageSize;

        if (info.reb) {
            List<HundredRebRecordInfo> list = DBManager.I.loadBatch(ETableType.TB_HUNDRED_REB_RECORD, new IDBLoad<HundredRebRecordInfo>() {
                @Override
                public String getRedisKey() {
                    return ETableType.TB_HUNDRED_REB_RECORD.getRedisKey();
                }

                @Override
                public List<HundredRebRecordInfo> load(IBaseDAO<HundredRebRecordInfo> dao) {
                    return ((IHundredRebRecordDAO) dao).loadByPlayerUid(info.roomId, player.getUid(), info.page * Constant.PAGE_CNT_10, Constant.PAGE_CNT_10);
                }
            });
            for (HundredRebRecordInfo recordInfo : list) {
                //只显示最近30条
                if (info.page >= 3) {
                    break;
                }
                PCLIHundredNtfRecord.RebRecord rebRecord = new PCLIHundredNtfRecord.RebRecord();
                rebRecord.time = recordInfo.getTime();
                rebRecord.bankerCardType = recordInfo.getBankerCardType();
                for (int i = 0, len = recordInfo.getRebInfo().size(); i < len; ++i) {
                    HundredRebRecordInfo.AllRebInfo temp = recordInfo.getRebInfo().get(i);
                    PCLIHundredNtfRecord.AllRebInfo allRebInfo = new PCLIHundredNtfRecord.AllRebInfo();
                    allRebInfo.cardType = temp.getCardType();
                    allRebInfo.cards = temp.getCards();
                    Iterator<Map.Entry<EHundredArenaRebType, HundredRebRecordInfo.RebInfo>> it = temp.getAllReb().entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<EHundredArenaRebType, HundredRebRecordInfo.RebInfo> entry = it.next();
                        PCLIHundredNtfRecord.RebInfo rebInfo = new PCLIHundredNtfRecord.RebInfo();
                        rebInfo.rebValue = NumberUtils.get0Decimals(entry.getValue().getRebValue());
                        rebInfo.winValue = NumberUtils.get0Decimals(entry.getValue().getWinValue());
                        allRebInfo.allReb.put(entry.getKey().ordinal(), rebInfo);
                    }
                    rebRecord.rebInfo.add(allRebInfo);
                }
                record.rebList.add(rebRecord);
            }
            record.next = record.rebList.size() == Constant.PAGE_CNT_10;
        } else {
//            List<HundredBureauRecordInfo> list = DBManager.I.loadBatch(ETableType.TB_HUNDRED_BUREAU_RECORD, new IDBLoad<HundredBureauRecordInfo>() {
//                @Override
//                public String getRedisKey() {
//                    return ETableType.TB_HUNDRED_BUREAU_RECORD.getRedisKey();
//                }
//
//                @Override
//                public List<HundredBureauRecordInfo> load(IBaseDAO<HundredBureauRecordInfo> dao) {
//                    return ((IHundredBureauRecordDAO) dao).loadByRoomId(info.roomId, 0, info.pageSize);
//                }
//            });
            List<HundredBureauRecordInfo> list = hundredHandle.getRBureauRecordInfos();
            for (HundredBureauRecordInfo recordInfo : list) {
                PCLIHundredNtfRecord.BankerRecord bankerRecord = new PCLIHundredNtfRecord.BankerRecord();
                bankerRecord.time = recordInfo.getTime();
                for (int i = 0, len = recordInfo.getCardInfo().size(); i < len; ++i) {
                    HundredBureauRecordInfo.CardInfo temp = recordInfo.getCardInfo().get(i);
                    PCLIHundredNtfRecord.CardInfo cardInfo = new PCLIHundredNtfRecord.CardInfo();
                    cardInfo.cardType = temp.getCardType();
                    cardInfo.cards.addAll(temp.getCards());
                    cardInfo.win = temp.isWin();
                    cardInfo.cards = temp.getCards();
                    bankerRecord.cardInfo.add(cardInfo);
                }
                record.bankerList.add(bankerRecord);
            }
            record.next = false;
        }

        player.send(CommandId.CLI_NTF_ARENA_HUNDRED_RECORD_OK, record);
        return null;
    }
}
