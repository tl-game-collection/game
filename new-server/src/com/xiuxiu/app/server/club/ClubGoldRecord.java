package com.xiuxiu.app.server.club;

import com.xiuxiu.app.server.club.impl.AbstractClubValueRecord;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;

public class ClubGoldRecord extends AbstractClubValueRecord {
    public ClubGoldRecord() {
        this.tableType = ETableType.TB_CLUB_GOLD_RECORD;
    }

    @Override
    public String toString() {
        return "ClubGoldRecord{" +
                "playerUid=" + this.getPlayerUid() +
                ", action=" + this.getAction() +
                ", inMoney=" + this.getInMoney() +
                ", outMoney=" + this.getOutMoney() +
                ", beginAmount=" + this.getBeginAmount() +
                ", optPlayerUid=" + this.getOptPlayerUid() +
                ", mainClubUid=" + this.getMainClubUid() +
                ", clubUid=" + this.getClubUid() +
                ", createdAt=" + this.getCreatedAt() +
                ", uid=" + this.getUid() +
                '}';
    }
}