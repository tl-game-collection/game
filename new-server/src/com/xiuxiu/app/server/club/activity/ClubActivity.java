package com.xiuxiu.app.server.club.activity;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.server.club.activity.divide.ClubActivityDivideData;
import com.xiuxiu.app.server.club.activity.gold.ClubActivityGoldData;
import com.xiuxiu.app.server.club.constant.EClubActivityType;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

/**
 * 亲友圈活动配置数据
 * 
 * @author Administrator
 *
 */
public class ClubActivity extends BaseTable {

    /**
     * 亲友圈id
     */
    private long clubId;
    /**
     * 活动类型
     */
    private int type;

    /**
     * 活动配置数据
     */
    private String info;

    private transient Map<Long, ClubActivityGoldData> goldData = new HashMap<Long, ClubActivityGoldData>();
    private transient ClubActivityDivideData divideData = new ClubActivityDivideData();

    public ClubActivity() {
        this.setTableType(ETableType.TB_CLUB_ACTIVITY);
    }

    public String getInfo() {
        return EClubActivityType.DIVIDE.match(type) ? JSON.toJSONString(divideData) : JSON.toJSONString(goldData);
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void init() {
        if (StringUtil.isEmptyOrNull(info)) {
            return;
        }
        if (EClubActivityType.DIVIDE.match(type)) {
            ClubActivityDivideData temp = JsonUtil.fromJson(info, new TypeReference<ClubActivityDivideData>() {
            });
            if (null != temp) {
                this.divideData = temp;
            }
        } else {
            Map<Long, ClubActivityGoldData> temp = JsonUtil.fromJson(info,
                    new TypeReference<Map<Long, ClubActivityGoldData>>() {
                    });
            if (null != temp) {
                this.goldData = temp;
            }
        }
    }

    public Map<Long, ClubActivityGoldData> getGoldData() {
        return goldData;
    }

    public ClubActivityDivideData getDivideData() {
        return divideData;
    }

    public long getClubId() {
        return clubId;
    }

    public void setClubId(long clubId) {
        this.clubId = clubId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
