package com.xiuxiu.app.server.services.api.old;
import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.ForceLeaveArena;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

/**
 * 强制退出竞技场
 */
public class ForceLeaveArenaHandler extends BaseHttpHandler {
	@Override
	protected void doHandler(HttpExchange httpExchange) throws IOException {
		String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
		ForceLeaveArena info = JsonUtil.fromJson(body, ForceLeaveArena.class);
		Logs.API.debug("收到强制退出竞技场请求:%s", info);
		String sign = MD5Util.getMD5(info.playerUid, Config.APP_KEY);
		ErrorMsg resp = new ErrorMsg();
		do {
			 if (!sign.equalsIgnoreCase(info.sign)) {
			 resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
			 resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
			 break;
			 }
			if (0 == info.playerUid) {
				resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
				resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
				break;
			}
			Player player = PlayerManager.I.getPlayer(info.playerUid);
			if (null == player) {
				resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
				resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
				break;
			}
			player.changeRoomId(-1, -1);
		} while (false);
		HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
		httpExchange.close();
	}
}
