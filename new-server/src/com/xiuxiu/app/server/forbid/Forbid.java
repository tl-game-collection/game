package com.xiuxiu.app.server.forbid;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

/**
 * 防作弊玩家信息
 * @author Administrator
 *
 */
public class Forbid extends BaseTable {

    /**
     * 亲友圈类型1房卡亲友圈2金币亲友圈
     */
    private Integer clubType;
    /**
     * 群组id
     */
    private long clubUid;
    /**
     * 防作弊玩家id集合
     */
    private String playerUids;

    /**
     * 防作弊标识，0本圈，1总圈
     */
    private boolean flag;

    private Long[] playerUidList;

    public Forbid() {
        this.tableType = ETableType.TB_FORBID;
    }

    public Integer getClubType() {
        return clubType;
    }

    public void setClubType(Integer clubType) {
        this.clubType = clubType;
    }

    public long getClubUid() {
        return clubUid;
    }

    public void setClubUid(long clubUid) {
        this.clubUid = clubUid;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setPlayerUids(String playerUids) {
        if (StringUtil.isEmptyOrNull(playerUids)) {
            return;
        }
        Long[] temp = JsonUtil.fromJson(playerUids, new TypeReference<Long[]>() {
        });
        if (null != temp) {
            this.playerUidList = temp;
        }
    }

    public String getPlayerUidsDb() {
        return JsonUtil.toJson(this.playerUidList);
    }

    public Long[] getPlayerUidList() {
        return playerUidList;
    }

}
