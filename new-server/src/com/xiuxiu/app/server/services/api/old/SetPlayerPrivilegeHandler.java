package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.SetPlayerPrivilegeInfo;
import com.xiuxiu.app.protocol.api.SetPlayerPrivilegeInfoResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

public class SetPlayerPrivilegeHandler extends BaseHttpHandler {
	@Override
	protected void doHandler(HttpExchange httpExchange) throws IOException {
		String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
		SetPlayerPrivilegeInfo info = JsonUtil.fromJson(body, SetPlayerPrivilegeInfo.class);
		Logs.API.debug("设置玩家授权:%s", info);
		SetPlayerPrivilegeInfoResp resp = new SetPlayerPrivilegeInfoResp();
		String sign = MD5Util.getMD5(info.playerUid, info.hasPrivilege, Config.APP_KEY);
		do {
			if (!sign.equals(info.sign)) {
		        resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
		        resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
		        break;
			}
			Player player =  PlayerManager.I.getPlayer(info.playerUid);
			if (null == player) {
				resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
		        resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
		        break;
			}
			player.setEmpower(info.hasPrivilege!=0?true:false);
			player.setDirty(true);
			resp.ret = ErrorCode.OK.getRet();
	        resp.msg = ErrorCode.OK.getMsg();
		} while (false);
		HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
		httpExchange.close();
	}
}
