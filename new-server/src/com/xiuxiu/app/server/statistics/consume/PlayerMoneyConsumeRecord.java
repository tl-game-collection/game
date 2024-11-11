package com.xiuxiu.app.server.statistics.consume;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.core.utils.JsonUtil;

public class PlayerMoneyConsumeRecord extends BaseTable {
    private static final long serialVersionUID = 2831152635920857310L;
    private long playerUid;
    private float value1;
    private float value2;
    private float value3;
    private PlayerMoneyConsumeMonthData monthValue1 = new PlayerMoneyConsumeMonthData();
    private PlayerMoneyConsumeMonthData monthValue2 = new PlayerMoneyConsumeMonthData();
    private PlayerMoneyConsumeMonthData monthValue3 = new PlayerMoneyConsumeMonthData();

    public PlayerMoneyConsumeRecord() {
        this.tableType = ETableType.TB_PLAYER_MONEY_CONSUME_RECORD;
    }
    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public float getValue1() {
        return value1;
    }

    public void setValue1(float value1) {
        this.value1 = value1;
    }

    public float getValue2() {
        return value2;
    }

    public void setValue2(float value2) {
        this.value2 = value2;
    }

    public float getValue3() {
        return value3;
    }

    public void setValue3(float value3) {
        this.value3 = value3;
    }

    public String getMonthValue1Db() {
        return JsonUtil.toJson(this.monthValue1);
    }

    public void setMonthValue1Db(String rule) {
        this.monthValue1 = JsonUtil.fromJson(rule, new TypeReference<PlayerMoneyConsumeMonthData>() {
        });
    }

    public String getMonthValue2Db() {
        return JsonUtil.toJson(this.monthValue2);
    }

    public void setMonthValue2Db(String rule) {
        this.monthValue2 = JsonUtil.fromJson(rule, new TypeReference<PlayerMoneyConsumeMonthData>() {
        });
    }

    public String getMonthValue3Db() {
        return JsonUtil.toJson(this.monthValue3);
    }

    public void setMonthValue3Db(String rule) {
        this.monthValue3 = JsonUtil.fromJson(rule, new TypeReference<PlayerMoneyConsumeMonthData>() {
        });
    }

    public PlayerMoneyConsumeMonthData getMonthValue1() {
        return monthValue1;
    }

    public PlayerMoneyConsumeMonthData getMonthValue2() {
        return monthValue2;
    }

    public PlayerMoneyConsumeMonthData getMonthValue3() {
        return monthValue3;
    }

}
