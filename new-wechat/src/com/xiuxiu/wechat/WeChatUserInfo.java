package com.xiuxiu.wechat;

public class WeChatUserInfo {
    private String uid;
    private String nick;
    private byte sex;   // 1: 男, 2: 女
    private String icon;
    private String country;
    private String city;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public byte getSex() {
        return sex;
    }

    public void setSex(byte sex) {
        this.sex = sex;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "WeChatUserInfo{" +
                "uid='" + uid + '\'' +
                ", nick='" + nick + '\'' +
                ", sex=" + sex +
                ", icon='" + icon + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
