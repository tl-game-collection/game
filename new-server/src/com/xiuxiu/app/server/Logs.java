package com.xiuxiu.app.server;

import com.xiuxiu.core.log.Log;
import com.xiuxiu.core.log.LogFactory;

public interface Logs extends com.xiuxiu.core.log.Logs {
    Log API = LogFactory.get("api");
    Log LOGIN = LogFactory.get("login");
    Log DB = LogFactory.get("db");
    Log ACCOUNT = LogFactory.get("account");
    Log PLAYER = LogFactory.get("player");
    Log GROUP = LogFactory.get("group");
    Log ROOM = LogFactory.get("room");
    Log CHAT = LogFactory.get("chat");
    Log ARENA = LogFactory.get("arena");
    Log MAIL = LogFactory.get("mail");
    Log RANK = LogFactory.get("rank");
    Log ONLINE = LogFactory.get("online");
    Log PLAYER_LOGIN = LogFactory.get("playerLogin");
    Log PLAYER_LOGOUT = LogFactory.get("playerLogout");
    Log CLUB = LogFactory.get("club");
}
