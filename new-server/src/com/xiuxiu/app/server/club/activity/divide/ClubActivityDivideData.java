package com.xiuxiu.app.server.club.activity.divide;

import java.util.ArrayList;
import java.util.List;

/**
 * 奖励分分成比例
 * 
 * @author Administrator
 *
 */
public class ClubActivityDivideData {

    /**
     * 是否开启
     */
    private boolean open;
    
    /**
     * 基本获取分成比例
     */
    private ClubActivityDivideDataItem base = new ClubActivityDivideDataItem();
    /**
     * 奖励分成比例每个档位数据
     */
    private List<ClubActivityDivideDataItem> items = new ArrayList<>();

    public ClubActivityDivideDataItem getBase() {
        return base;
    }

    public void setBase(ClubActivityDivideDataItem base) {
        this.base = base;
    }

    public List<ClubActivityDivideDataItem> getItems() {
        return items;
    }

    public void setItems(List<ClubActivityDivideDataItem> items) {
        this.items = items;
    }

    public ClubActivityDivideDataItem getByRewardValue(Long rewardValue) {
        ClubActivityDivideDataItem result = null;
        for (ClubActivityDivideDataItem tempItem : items) {
            if (rewardValue < tempItem.getNeedValue()) {
                continue;
            }
            if (null == result || tempItem.getNeedValue() > result.getNeedValue()) {
                result = tempItem;
            }
        }
        return null == result ? base : result;
    }
    

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    

}
