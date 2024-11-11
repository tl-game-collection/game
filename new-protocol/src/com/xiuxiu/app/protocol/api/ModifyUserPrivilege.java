package com.xiuxiu.app.protocol.api;

public class ModifyUserPrivilege {
    public long uid;
    public int level;
    public String sign; // md5(uid + level + add + key)

    @Override
    public String toString() {
        return "ModifyUserPrivilege{" +
                "uid=" + uid +
                ", level=" + level +
                ", sign='" + sign + '\'' +
                '}';
    }
}
