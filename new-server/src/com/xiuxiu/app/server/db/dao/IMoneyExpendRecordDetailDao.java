package com.xiuxiu.app.server.db.dao;

import java.util.List;

import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendRecordDetail;

public interface IMoneyExpendRecordDetailDao extends IBaseDAO<MoneyExpendRecordDetail> {

    float getTotalConsumeByClubUid(long clubUid);

    int countByClubUid(long clubUid);

    List<MoneyExpendRecordDetail> getByClubUid(long clubUid, int beginPag, int endPag);

    int countByClubUidAndTime(long clubUid, String time);

    List<MoneyExpendRecordDetail> getByClubUidAndTime(long clubUid, String time, int beginPag, int endPag);

    int batchInsert(List<MoneyExpendRecordDetail> valueList);

}
