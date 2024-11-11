package com.xiuxiu.app.server.services.api.robot;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.ClubGetListResp;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.constant.EBoxState;
import com.xiuxiu.app.server.box.handle.IBoxHandle;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;

public class GetPlaysHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        long clubUid = Long.parseLong(body);
        Logs.API.debug("查询该clubUid所有玩法列表:%s", clubUid);
        ClubGetListResp resp = new ClubGetListResp();
        do {
        	// 加载该clubUid所有玩法列表
        	IClub club = ClubManager.I.getClubByUid(clubUid);
        	if (club == null) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
        	Map<Long, Box> allBox = club.getAllBox();
        	Iterator<Map.Entry<Long, Box>> it = allBox.entrySet().iterator();
            JSONArray rooms = new JSONArray();
            JSONArray plays = new JSONArray();
            while (it.hasNext()) {
            	Entry<Long, Box> entry = it.next();
                Box box = entry.getValue();
                if (EBoxState.INIT == box.getState()) {
                	Long boxUid = entry.getKey();
            		int gameType = box.getGameType();
            		int gameSubType = box.getGameSubType();
            		if(gameType<20000) {
            			continue;
            		}
            		if(gameType>=30000) {
            			continue;
            		}
            		if(gameType!=20003 && gameType!=20013) {
            			continue;
            		}
            		JSONObject js = new JSONObject();
    				String boxName = "";
            		if(gameType==20003) {
            			int cowKingRazzType = box.getRule().get("cowKingRazzType");
            			if(gameSubType==1) {
            				if(cowKingRazzType==1||cowKingRazzType==2) {
            					boxName = "拼十_斗公牛_普通场";
            				}else if(cowKingRazzType==3) {
            					boxName = "拼十_斗公牛_疯狂王癞";
            				}else if(cowKingRazzType==4) {
            					boxName = "拼十_斗公牛_随机癞子";
            				}
            			}else if(gameSubType==2) {
            				if(cowKingRazzType==1||cowKingRazzType==2) {
            					boxName = "拼十_明牌抢庄_普通场";
            				}else if(cowKingRazzType==3) {
            					boxName = "拼十_明牌抢庄_疯狂王癞";
            				}else if(cowKingRazzType==4) {
            					boxName = "拼十_明牌抢庄_随机癞子";
            				}
            			}
            		}else if(gameType==20005) {
//            			boxName = "拼三张";
            		}else if(gameType==20008) {
//            			boxName = "十三水";
            		}else if(gameType==20012) {
//            			boxName = "牌九";
            		}else if(gameType==20013) {
            			boxName = "三公";
            			if(gameSubType==1) {
            				boxName = "三公_明牌抢庄";
            			}else if(gameSubType==2) {
            				boxName = "三公_自由抢庄";
            			}else if(gameSubType==3) {
            				boxName = "三公_通比玩法";
            			}else if(gameSubType==4) {
            				boxName = "三公_三公当庄";
            			}else if(gameSubType==5) {
            				boxName = "三公_三公比金花";
            			}
            		}
            		js.put("boxName", boxName);
            		js.put("boxUid", boxUid);
            		plays.add(js);
            		
            		if(box.getRoomIds().size()>0) {
            			for(Long roomUid : box.getRoomIds()) {
            				IBoxHandle boxHandle =  box.getBoxHandle(roomUid);
            				IRoom room = boxHandle.getRoom();
                    		js.put("boxName", boxName);
                    		if(room!=null) {
                    			js.put("roomId", room.getRoomId());
                    		}else {
                    			js.put("roomId", "无房间");
                    		}
                    		rooms.add(js);
                		}
            		}
                }
            }
            JSONObject json = new JSONObject();
            json.put("plays", plays);
            json.put("rooms", rooms);
            System.out.println("plays:"+json.toJSONString());
            HttpServer.sendOk(httpExchange, json.toJSONString().getBytes(Charsetutil.UTF8));
            httpExchange.close();
        } while (false);
    }
}
