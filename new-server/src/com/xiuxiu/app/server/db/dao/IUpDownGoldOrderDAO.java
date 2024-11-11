package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.order.UpDownGoldOrder;

import java.util.List;

public interface IUpDownGoldOrderDAO extends IBaseDAO<UpDownGoldOrder> {
    List<UpDownGoldOrder> loadByParms(long playerUid, long uid, long clubUid, int state, long createAt, int begin, int size);

    List<UpDownGoldOrder> loadByPlayerUidAndState(long playerUid, long clubUid, long state, int begin, int size, long minTime);

    List<UpDownGoldOrder> loadByOptPlayerUidAndState(long optPlayerUid, long mainClubUid, long state, int begin, int size, long minTime);

    List<UpDownGoldOrder> loadByState(int state);

    List<UpDownGoldOrder> loadByMainClubUid(long mainClubUid);
}
