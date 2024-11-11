package com.xiuxiu.app.server.services.api.robot;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.ClubGetListResp;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubInfo;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;

public class GetRobotsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        JSONObject js = JSONObject.parseObject(body);
        int page1 = js.getIntValue("page1");
        int size = js.getIntValue("size");
        
        Logs.API.debug("查询该页所有机器人列表:%s", page1);
        ClubGetListResp resp = new ClubGetListResp();
        do {
        	// 加载该页所有机器人列表
        	List<Player> list = DBManager.I.getPlayerDao().loadAllRobot(page1, size);
//            List<ClubMember> memberList = DBManager.I.getClubMemberDAO().loadAllClubByPlayerUid(playerUid);
//            if (memberList == null) {
//                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
//                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
//                break;
//            }
//            JSONArray array = new JSONArray();
//            for(ClubMember clubMember : memberList) {
//            	long clubUid = clubMember.getClubUid();
//            	//根据clubUid获取俱乐部信息
//            	ClubInfo clubInfo = DBManager.I.getClubInfoDAO().loadByUid(clubUid);
//            	//判断哪些是比赛场俱乐部
//            	if(clubInfo.getClubType()==2) {
//            		JSONObject js = new JSONObject();
//            		js.put("clubUid", clubUid);
//            		String name = clubInfo.getName();
//            		js.put("name", name);
//            		array.add(js);
//            	}
//            }
//            JSONObject json = new JSONObject();
//            json.put("clubs", array);
//            System.out.println("clubs:"+json.toJSONString());
//            HttpServer.sendOk(httpExchange, json.toJSONString().getBytes(Charsetutil.UTF8));
//            httpExchange.close();
        } while (false);
    }
}
