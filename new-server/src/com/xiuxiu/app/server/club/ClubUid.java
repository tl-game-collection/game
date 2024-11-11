package com.xiuxiu.app.server.club;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;

public class ClubUid extends BaseTable {
    private int good;
    private int state;

    public ClubUid() {
        this.tableType = ETableType.TB_CLUB_UID;
    }

    public int getGood() {
        return good;
    }
    public void setGood(int good) {
        this.good = good;
    }

    public int getState() { return state; }
    public void setState(int state) { this.state = state; }
}
