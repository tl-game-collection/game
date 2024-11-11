package com.xiuxiu.app.server.club;

import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;
import com.xiuxiu.app.server.club.constant.EConvertType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.order.UpDownGoldOrder;

import java.util.List;
import java.util.Set;

/**
 * @auther: yuyunfei
 * @date: 2020/1/6 11:19
 * @comment:
 */
public class DBReplaceManager {
    private static class DBReplaceManagerHolder {
        private static DBReplaceManager instance = new DBReplaceManager();
    }

    public static DBReplaceManager I = DBReplaceManager.DBReplaceManagerHolder.instance;

    public void init(){
        //this.ReplaceMemberConvert();
        //this.ReplaceDownOrderMainClubUid();
    }

    // 修改所有群成员 用房卡兑换成竞技分
    private void ReplaceMemberConvert() {
        Set<Long> clubUid = ClubManager.I.getAllClubIds();
        for (Long uid : clubUid) {
            IClub iClub = ClubManager.I.getClubByUid(uid);
            if (null == iClub) {
                continue;
            }
            iClub.foreach(members -> {
                if (members[0].getConvert() == EConvertType.CONVERT.ordinal()) {
                    ClubMemberExt clubMemberExt = iClub.getMemberExt(members[0].getPlayerUid(), true);
                    clubMemberExt.setConvert(members[0].getConvert());
                    clubMemberExt.setDirty(Boolean.TRUE);
                }
            });
            this.ReplaceClubDToGoldTotal(uid, iClub);//统计圈成员兑换竞技值总值
        }
    }

    private void ReplaceClubDToGoldTotal(long clubUid, IClub iClub) {
//        int action = EClubGoldChangeType.EXCHANGE_CONVERT_VALUE_INC.getValue();
//        long total = DBManager.I.getClubGoldRecordDAO().loadClubGoldRecordCounInMoney(clubUid, action);
//        iClub.getClubInfo().setdToGoldTotal(total);
//        iClub.getClubInfo().setDirty(Boolean.TRUE);
    }

    private void ReplaceDownOrderMainClubUid() {
        List<UpDownGoldOrder> tempList = DBManager.I.getUpDownGoldOrderDao().loadByMainClubUid(-1);
        for (UpDownGoldOrder tempOrder : tempList) {
            if (tempOrder == null) {
                continue;
            }
            if (tempOrder.getMainClubUid() > 0){
                continue;
            }
            IClub iClub = ClubManager.I.getClubByUid(tempOrder.getClubUid());
            if (iClub == null) {
                continue;
            }
            long rootClubUid = tempOrder.getClubUid();
            if (iClub.checkIsJoinInMainClub() && !iClub.checkIsMainClub()) {
                rootClubUid = iClub.getFinalClubId();
            }
            tempOrder.setMainClubUid(rootClubUid);
            tempOrder.setDirty(true);
            tempOrder.save();
        }
    }
}
