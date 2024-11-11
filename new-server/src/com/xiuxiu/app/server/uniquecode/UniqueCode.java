package com.xiuxiu.app.server.uniquecode;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;

public class UniqueCode extends BaseTable {
    public UniqueCode() {
        this.tableType = ETableType.TB_UNIQUE_CODE;
    }
    /**
     * EUniqueCode
     */
    private int type;
    /**
     * 每种类型对应的唯一码
     */
    private long code;
    /**
     * 唯一码对应的其他参数
     */
    private String param;
    /**
     * 唯一码状态
     */
    private int state;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
