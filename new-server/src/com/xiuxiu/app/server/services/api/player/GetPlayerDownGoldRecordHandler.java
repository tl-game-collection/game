package com.xiuxiu.app.server.services.api.player;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.player.GetPlayerDownGoldRecord;
import com.xiuxiu.app.protocol.api.temp.player.GetPlayerDownGoldRecordResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.order.UpDownGoldOrder;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;
import java.util.List;

/**
 * @auther: luocheng
 * @date: 2020/1/8 15:34
 */
public class GetPlayerDownGoldRecordHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("获取玩家财务下分记录");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetPlayerDownGoldRecord info = JsonUtil.fromJson(body, GetPlayerDownGoldRecord.class);
        String sign = MD5Util.getMD5(info.playerUid, info.orderId, info.clubUid, info.state, info.time, info.page, info.pageSize, Config.APP_KEY);
        GetPlayerDownGoldRecordResp resp = new GetPlayerDownGoldRecordResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (info.state < 0 || info.state > 2) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            List<UpDownGoldOrder> list = DBManager.I.getUpDownGoldOrderDao().loadByParms(info.playerUid, info.orderId, info.clubUid, info.state, info.time, info.page * info.pageSize, info.pageSize + 1);
            int count = 0;
            for (UpDownGoldOrder tempOrder : list) {
                count++;
                if (count > info.pageSize) {
                    break;
                }
                GetPlayerDownGoldRecordResp.DownGoldOrder downGoldOrder = new GetPlayerDownGoldRecordResp.DownGoldOrder();
                downGoldOrder.orderId = tempOrder.getUid();
                downGoldOrder.playerUid = tempOrder.getPlayerUid();
                Player tempPlayer = PlayerManager.I.getPlayer(tempOrder.getPlayerUid());
                if (tempPlayer != null) {
                    downGoldOrder.playerName = tempPlayer.getName();
                }
                downGoldOrder.clubUid = tempOrder.getClubUid();
                downGoldOrder.value = tempOrder.getValue();
                downGoldOrder.chargeValue = tempOrder.getChargeValue();
                downGoldOrder.bankCard = tempOrder.getBankCard();
                downGoldOrder.bankCardHolder = tempOrder.getBankCardHolder();
                downGoldOrder.createAt = tempOrder.getCreateAtDetail();
                downGoldOrder.state = tempOrder.getState();
                downGoldOrder.optPlayerUid = tempOrder.getOptPlayerUid();

                resp.list.add(downGoldOrder);
            }
            resp.page = info.page;
            resp.pageSize = info.pageSize;
            resp.next = list.size() == info.pageSize + 1;
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
