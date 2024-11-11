package com.xiuxiu.app.server.system;

import com.xiuxiu.app.server.db.BaseTable;

public class AssistantWeChat extends BaseTable {
    private String weChat;                              // 客服微信号
    private String province;                            // 省
    private String city;                                // 市
    private String district;                            // 区
    private long adCode;                                // 区域编码：用于排序

    public String getWeChat() {
        return weChat;
    }

    public void setWeChat(String weChat) {
        this.weChat = weChat;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public long getAdCode() {
        return adCode;
    }

    public void setAdCode(long adCode) {
        this.adCode = adCode;
    }
}
