package com.xiuxiu.app.server.services.api.notice;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.notice.Notice;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;

public class GetNoticesHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
    	List<Notice> list = DBManager.I.getNoticeDao().loadAll();
//    	String str = "";
//    	for(Notice notice : list) {
//    		if(str.equals("")) {
//    			str = "{{type=\""+notice.getType()+"\",content=\""+notice.getContent()+"\"}";
//    		}else {
//    			str += ",{type=\""+notice.getType()+"\",content=\""+notice.getContent()+"\"}";
//    		}
//    	}
//    	str += "}";
    	JSONObject json = new JSONObject();
    	json.put("data", list);
    	HttpServer.sendOk(httpExchange, json.toJSONString().getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
