package com.xiuxiu.app.server.db.dao;

import java.util.List;

import com.xiuxiu.app.server.room.normal.Hundred.HundredBureauRecordInfo;

public interface IHundredBureauRecordDAO extends IBaseDAO<HundredBureauRecordInfo> {
    List<HundredBureauRecordInfo> loadByRoomId(long roomId, int begin, int size);
    List<HundredBureauRecordInfo> loadBankerByRoomId(long roomId,long bankerUid, int begin, int size);
}
