package com.xiuxiu.app.server.account;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;

public class AccountUid extends BaseTable {
    private int good;
    private int state;

    public AccountUid() {
        this.tableType = ETableType.TB_ACCOUNT_UID;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
