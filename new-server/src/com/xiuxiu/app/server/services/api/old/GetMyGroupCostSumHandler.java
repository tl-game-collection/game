package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GetMyGroupCostSumHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
//        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
//        GetMyGroupCostSum info = JsonUtil.fromJson(body, GetMyGroupCostSum.class);
//        Logs.API.debug("根据用户id查询所有群钻石总消耗:%s", info);
//        String sign = MD5Util.getMD5(info.uid, Config.APP_KEY);
//        GetMyGroupCostSumResp resp = new GetMyGroupCostSumResp();
//        do {
//            if (!sign.equalsIgnoreCase(info.sign)) {
//                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
//                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
//                break;
//            }
//            Player player = PlayerManager.I.getPlayer(info.uid);
//            if (null == player) {
//                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
//                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
//                break;
//            }
//            IGroupArenaCostDiamondDetailDAO dao = DBManager.I.getGroupArenaCostDiamondDetailDao();
//            GetMyGroupCostSumResp.CostSum costSum = new GetMyGroupCostSumResp.CostSum();
//            costSum.sumCost = dao.sumCostByOwner(info.uid);
//            costSum.sign = MD5Util.getMD5(costSum.sumCost, Config.APP_KEY);
//            resp.data = costSum;
//        } while (false);
//
//        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
//        httpExchange.close();
    }
}
