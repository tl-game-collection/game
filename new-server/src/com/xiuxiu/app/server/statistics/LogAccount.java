package com.xiuxiu.app.server.statistics;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;

public class LogAccount extends BaseTable {
    private long targetUid;
    private int action; // see AccountActionEnum
    private long timestamp;
    private int accountType;
    private int serverId;
    private String deviceModel;
    private String deviceSn;
    private String osVersion;
    private String address;
    private String appVersion;
    private int channelId;
    private String mobileNumber;

    public LogAccount() {
        this.tableType = ETableType.TB_LOG_ACCOUNT;
    }

    public long getTargetUid() {
        return targetUid;
    }

    public void setTargetUid(long targetUid) {
        this.targetUid = targetUid;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Override
    public String toString() {
        return "LogAccount{" +
                "targetUid=" + targetUid +
                ", action=" + action +
                ", timestamp=" + timestamp +
                ", accountType=" + accountType +
                ", serverId=" + serverId +
                ", deviceModel='" + deviceModel + '\'' +
                ", deviceSn='" + deviceSn + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", address='" + address + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", channelId=" + channelId +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", isNew=" + isNew +
                ", tableType=" + tableType +
                ", uid=" + uid +
                ", dirty=" + dirty +
                '}';
    }
}
