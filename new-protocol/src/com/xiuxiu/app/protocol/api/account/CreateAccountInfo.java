package com.xiuxiu.app.protocol.api.account;

import java.util.ArrayList;
import java.util.List;

public class CreateAccountInfo {
    public static class AccountInfo {
        private long uid;
        private String role;
        private String password;
        private String name;
        private String icon;
        private String mac;
        private String phone;
        private String phoneVer;
        private String phoneOsVer;
        private String city;
        private String otherPlatformToken;

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
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

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getOtherPlatformToken() {
            return otherPlatformToken;
        }

        public void setOtherPlatformToken(String otherPlatformToken) {
            this.otherPlatformToken = otherPlatformToken;
        }

        @Override
        public String toString() {
            return "AccountInfo{" +
                    "uid=" + uid +
                    ", role='" + role + '\'' +
                    ", password='" + password + '\'' +
                    ", name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", mac='" + mac + '\'' +
                    ", phone='" + phone + '\'' +
                    ", phoneVer='" + phoneVer + '\'' +
                    ", phoneOsVer='" + phoneOsVer + '\'' +
                    ", city='" + city + '\'' +
                    ", otherPlatformToken='" + otherPlatformToken + '\'' +
                    '}';
        }
    }

    private List<AccountInfo> entities = new ArrayList<>();
    private String sign;

    public List<AccountInfo> getEntities() {
        return entities;
    }

    public void setEntities(List<AccountInfo> entities) {
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
        return "CreateAccountInfo{" +
                "entities=" + entities +
                ", sign='" + sign + '\'' +
                '}';
    }
}
