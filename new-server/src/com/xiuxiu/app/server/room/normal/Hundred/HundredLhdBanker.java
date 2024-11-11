package com.xiuxiu.app.server.room.normal.Hundred;

import com.xiuxiu.app.server.player.IPlayer;

import java.util.HashMap;

public class HundredLhdBanker extends HundredBanker implements IHundredBanker {
    public static final String KEY_VALUE = "value";

    public HundredLhdBanker(long bankerUid, IPlayer player, HashMap<String, Integer> param) {
        super(bankerUid, player, param);
        this.value = this.param.getOrDefault(KEY_VALUE, 0) * 100;
    }

    @Override
    public boolean isSystem() {
        return false;
    }

    @Override
    public int getScore() {
        return this.value;
    }
}
