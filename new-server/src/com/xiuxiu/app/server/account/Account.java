package com.xiuxiu.app.server.account;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;

public class Account extends BaseTable {
    protected String name;
    protected String icon;
    protected byte sex;
    protected String city;
    protected String identityCard;
    protected long createTime;
    protected String mac;
    protected String phone;
    protected String phoneVer;
    protected String phoneOsVer;
    protected String passwd;
    protected String otherPlatformToken;
    protected byte type;                    // 注册账号类型, 0: 游客登陆, 1: 手机号登陆, 2: 快速登陆, 3: 微信登陆
    protected String payPassword;           // 支付密码
    protected int state;                    // 状态, 0: 正常, 1: 删除, 2: 封号
    protected int noNeedPayPassword;        // 免密支付状态：0:关闭 1:开启

    public Account() {
        this.tableType = ETableType.TB_ACCOUNT;
    }

    public String getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
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

    public byte getSex() {
        return sex;
    }

    public void setSex(byte sex) {
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

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getOtherPlatformToken() {
        return otherPlatformToken;
    }

    public void setOtherPlatformToken(String otherPlatformToken) {
        this.otherPlatformToken = otherPlatformToken;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getNoNeedPayPassword() {
        return noNeedPayPassword;
    }

    public void setNoNeedPayPassword(int noNeedPayPassword) {
        this.noNeedPayPassword = noNeedPayPassword;
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", sex=" + sex +
                ", city='" + city + '\'' +
                ", identityCard='" + identityCard + '\'' +
                ", createTime=" + createTime +
                ", mac='" + mac + '\'' +
                ", phone='" + phone + '\'' +
                ", phoneVer='" + phoneVer + '\'' +
                ", phoneOsVer='" + phoneOsVer + '\'' +
                ", passwd='" + passwd + '\'' +
                ", otherPlatformToken='" + otherPlatformToken + '\'' +
                ", type=" + type +
                ", payPassword='" + payPassword + '\'' +
                ", state=" + state +
                ", noNeedPayPassword=" + noNeedPayPassword +
                ", isNew=" + isNew +
                ", tableType=" + tableType +
                ", uid=" + uid +
                ", dirty=" + dirty +
                '}';
    }
}
