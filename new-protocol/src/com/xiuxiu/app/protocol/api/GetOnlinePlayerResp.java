package com.xiuxiu.app.protocol.api;

import java.util.List;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class GetOnlinePlayerResp extends ErrorMsg {
    public static class OnlinePlayer {
        public long uid;                // 玩家ID
        public String name;             // 玩家名称
        public byte type;               // 登录类型
        public String phone;            // 手机号登录就有手机号码否则为 null
        public int diamond;             // 房卡
        public long referrerUid;        // 推荐人ID
        public String recommend;//推荐信息
        public long createTimestamp;         // 注册时间
        public String createTim;
        public long lastLoginTime;      // 最后登录时间
        public String lastLoginIp;      // 最后登录IP
        public int state;               // 状态, 0: 正常, 1: 删除, 2: 封号  
    }
    
    public List<OnlinePlayer> list;
 
}
