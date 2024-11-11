package com.xiuxiu.app.server.player;

import com.xiuxiu.app.server.db.BaseTable;

public class Nickname extends BaseTable {
    public String name;
    public int state;

    @Override
    public String toString() {
        return "Nickname{" +
                "name='" + name + '\'' +
                ", state=" + state +
                '}';
    }
}
