package com.xiuxiu.app.protocol.api;

public class SetPlayerPrivilegeInfo {
    public long playerUid;
    public String sign;
    public int hasPrivilege;//是否有权限 0-无权限; 1-有权限;
    @Override
    public String toString() {
        return "SetPlayerPrivilegeInfo{" +
                "playerUid=" + playerUid +
                ", hasPrivilege=" + hasPrivilege +
                ", sign='" + sign + '\'' +
                '}';
    }
}
