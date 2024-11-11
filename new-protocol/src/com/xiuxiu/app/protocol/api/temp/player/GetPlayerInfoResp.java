package com.xiuxiu.app.protocol.api.temp.player;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GetPlayerInfoResp extends ErrorMsg {
    public static class PlayerInfo {
        public long uid;                // 玩家ID
        public String name;             // 玩家名称
        public byte type;               // 登录类型
        public String phone;            // 手机号登录就有手机号码否则为 null
        public int diamond;             // 房卡
        public long referrerUid;        // 推荐人ID
        public long enrollTime;         // 注册时间
        public long lastLoginTime;      // 最后登录时间
        public String lastLoginIp;      // 最后登录IP
        public int state;               // 状态, 0: 正常, 1: 删除, 2: 封号
        public String sign;             // md5(uid + name + type + phone + diamond + referrerUid + enrollTime + lastLoginTime + lastLoginIp + state + key)

        @Override
        public String toString() {
            return "PlayerInfo{" +
                    "uid=" + uid +
                    ", name='" + name + '\'' +
                    ", type=" + type +
                    ", phone='" + phone + '\'' +
                    ", diamond=" + diamond +
                    ", referrerUid=" + referrerUid +
                    ", enrollTime=" + enrollTime +
                    ", lastLoginTime=" + lastLoginTime +
                    ", lastLoginIp='" + lastLoginIp + '\'' +
                    ", state=" + state +
                    ", sign='" + sign + '\'' +
                    '}';
        }
    }

    public long count;
    public List<PlayerInfo> list = new ArrayList<>();
    public String sign;

    @Override
    public String toString() {
        return "GetPlayerInfoResp{" +
                "list=" + list +
                ", count=" + count +
                ", sign=" + sign +
                '}';
    }
}
