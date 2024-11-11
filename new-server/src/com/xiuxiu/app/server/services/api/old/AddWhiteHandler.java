
package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.AddWhite;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;
public class AddWhiteHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer. readBody(httpExchange), Charsetutil.UTF8);
        AddWhite info = JsonUtil.fromJson(body, AddWhite.class);
        Logs.API.debug("添加白名单:%s", info);
        String sign = MD5Util.getMD5(info.getPlayerUid(), Config.APP_KEY);
        ErrorMsg resp = new ErrorMsg();
        do {
            if (!sign.equalsIgnoreCase(info.getSign())) {
                Logs.API.warn("数据被串改");
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            long [] ids = new long[] {623275L,442881L,352954L,855814L};
            for(long id :ids)
            {
            	Player p = PlayerManager.I.getPlayer(id);
            	if(p!=null)
            	{
            		p.changeRoomId(-1, -1);
            	}
            }
            
        } while (false);
        
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}