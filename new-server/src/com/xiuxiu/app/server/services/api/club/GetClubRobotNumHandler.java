package com.xiuxiu.app.server.services.api.club;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.AddClubGameDesk;
import com.xiuxiu.app.protocol.api.temp.club.AddClubGameDeskResp;
import com.xiuxiu.app.protocol.api.temp.club.AddClubMember;
import com.xiuxiu.app.protocol.api.temp.club.GetClubRobotNumResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.floor.Floor;
import com.xiuxiu.app.server.player.EPlayerPrivilege;
import com.xiuxiu.app.server.player.EPlayerPrivilegeLevel;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

public class GetClubRobotNumHandler implements HttpHandler {
    
    
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Logs.API.debug("收到获取自己是圈主的所有圈信息请求");
            String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
            AddClubGameDesk info = JsonUtil.fromJson(body, AddClubGameDesk.class);
            String sign = MD5Util.getMD5(info.clubUid, info.floorUid,Config.APP_KEY);
            GetClubRobotNumResp resp = new  GetClubRobotNumResp();
            do {
                
                if (!sign.equalsIgnoreCase(info.sign)) {
                    resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                    resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                    break;
                }
               
           
                IClub iClub = ClubManager.I.getClubByUid(info.clubUid);
                
                
                if (iClub == null) {
                    resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                    resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                    break;
                }
               
                //楼层不存在
                Floor floor = iClub.getFloor(info.floorUid);
                if (null == floor) {
                    resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                    resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                    break;
                }
              
                   
                resp.clubUid = info.clubUid;
                resp.floorUid = info.floorUid;
                resp.curRobotDesk2=floor.getCurRobotDesk2();
                resp.curRobotDesk3=floor.getCurRobotDesk3();
                resp.curRobotDesk4=floor.getCurRobotDesk4();
               
            } while (false);

            HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
            httpExchange.close();
        }
     
    
    
}
