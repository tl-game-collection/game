package com.xiuxiu.app.protocol.api.account;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetAccountInfoResp extends ErrorMsg {
    public static class AccountInfo {
        private long uid;
        private long createTime;
        private String mac;
        private String phone;
        private String phoneVer;
        private String phoneOsVer;
        private String name;
        private String icon;
        private int sex;
        private String city;
        private String identityCard;
        private byte type;

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPhoneVer() {
            return phoneVer;
        }

        public void setPhoneVer(String phoneVer) {
            this.phoneVer = phoneVer;
        }

        public String getPhoneOsVer() {
            return phoneOsVer;
        }

        public void setPhoneOsVer(String phoneOsVer) {
            this.phoneOsVer = phoneOsVer;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getIdentityCard() {
            return identityCard;
        }

        public void setIdentityCard(String identityCard) {
            this.identityCard = identityCard;
        }

        public byte getType() {
            return type;
        }

        public void setType(byte type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "AccountInfo{" +
                    "uid=" + uid +
                    ", createTime=" + createTime +
                    ", mac='" + mac + '\'' +
                    ", phone='" + phone + '\'' +
                    ", phoneVer='" + phoneVer + '\'' +
                    ", phoneOsVer='" + phoneOsVer + '\'' +
                    ", name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", sex=" + sex +
                    ", city='" + city + '\'' +
                    ", identityCard='" + identityCard + '\'' +
                    ", type=" + type +
                    '}';
        }
    }

    public static class OnlineInfo {
        private int count;
        private long timestamp;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "OnlineInfo{" +
                    "count=" + count +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }

    public static class ActionRespInfo {
        private Object data;
        private String action;

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return "ActionRespInfo{" +
                    "data=" + data +
                    ", action='" + action + '\'' +
                    '}';
        }
    }

    private List<ActionRespInfo> entities = new ArrayList<>();
    private String sign;

    public List<ActionRespInfo> getEntities() {
        return entities;
    }

    public void setEntities(List<ActionRespInfo> entities) {
        this.entities = entities;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "GetAccountInfoResp{" +
                "entities=" + entities +
                ", sign='" + sign + '\'' +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
