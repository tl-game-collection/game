package com.xiuxiu.app.server.services.api.club;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.AddClubGameDesk;
import com.xiuxiu.app.protocol.api.temp.club.AddClubGameDeskResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.floor.Floor;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;
import java.util.List;

public class AddClubGameDeskHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到获取自己是圈主的所有圈信息请求");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        AddClubGameDesk info = JsonUtil.fromJson(body, AddClubGameDesk.class);
        String sign = MD5Util.getMD5(info.playerUid, info.clubUid, info.floorUid, info.type, info.robotDeskMin, info.robotDeskMax, info.randomTime, Config.APP_KEY);
        AddClubGameDeskResp resp = new AddClubGameDeskResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (info.randomTime < 5 || info.randomTime > 60) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (info.robotDeskMin < 0 || info.robotDeskMax < 0 || info.robotDeskMin > info.robotDeskMax) {
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
            //是否合圈
            if (iClub.checkIsJoinInMainClub()) {
                //不是总圈
                if (!iClub.checkIsMainClub()) {
                    resp.ret = ErrorCode.CLUB_NOT_MAIN_CLUB.getRet();
                    resp.msg = ErrorCode.CLUB_NOT_MAIN_CLUB.getMsg();
                    break;
                }
                //不是总圈主
                if (iClub.getOwnerId() != info.playerUid) {
                    resp.ret = ErrorCode.CLUB_NO_PRIVILEGE.getRet();
                    resp.msg = ErrorCode.CLUB_NO_PRIVILEGE.getMsg();
                    break;
                }
            } else {
                //不是圈主
                if (iClub.getOwnerId() != info.playerUid) {
                    resp.ret = ErrorCode.CLUB_NO_PRIVILEGE.getRet();
                    resp.msg = ErrorCode.CLUB_NO_PRIVILEGE.getMsg();
                    break;
                }
            }
            //楼层不存在
            Floor floor = iClub.getFloor(info.floorUid);
            if (null == floor) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            //圈内是否有足够的机器人
            int needPlayerCnt = 0;//需要几个机器人
            if (info.type==1) {
                if (info.robotDeskMax > floor.getCurRobotDesk2()) {
                    needPlayerCnt = (info.robotDeskMax - floor.getCurRobotDesk2()) * 2;
                }
            } 
            else if(info.type==2){
            
                  if (info.robotDeskMax > floor.getCurRobotDesk3()) {
                   needPlayerCnt = (info.robotDeskMax - floor.getCurRobotDesk3()) * 3;
                  }
                
            } else if(info.type==3) {

                if (info.robotDeskMax > floor.getCurRobotDesk4()) {
                 needPlayerCnt = (info.robotDeskMax - floor.getCurRobotDesk4()) * 4;
                
                }
            }  
                
                
           
            if (iClub.canUseRobotCnt() < needPlayerCnt && needPlayerCnt != 0) {
                resp.ret = ErrorCode.API_NOT_ENOUGH_ROBOT.getRet();
                resp.msg = ErrorCode.API_NOT_ENOUGH_ROBOT.getMsg();
                break;
            }
            List<Long> gameId = floor.getShowGameUid();
            if (info.type==1) {
                boolean bHas = false;//是否有2人场的玩法桌
                // 固定模式玩法桌
                for (long id : gameId) {
                    Box box = iClub.getBox(id);
                    if (null == box) {
                        continue;
                    }
                    if (EBoxType.NORMAL.match(box.getBoxType())) {
                        if (box.getRule().get("playerNum") == 2 && box.getRule().get("playerMinNum") == null) {
                            bHas = true;
                        }
                    }
                }
                if (!bHas && info.robotDeskMax != 0) {
                    resp.ret = ErrorCode.API_NOT_TWO_DESK.getRet();
                    resp.msg = ErrorCode.API_NOT_TWO_DESK.getMsg();
                    break;
                }
                floor.setSetRobotDesk2Min(info.robotDeskMin);
                floor.setSetRobotDesk2Max(info.robotDeskMax);
                floor.setRandomTime2(info.randomTime);
            } else if(info.type==2){
                
                {
                    boolean bHas = false;//是否有3人场的玩法桌
                    // 固定模式玩法桌
                    for (long id : gameId) {
                        Box box = iClub.getBox(id);
                        if (null == box) {
                            continue;
                        }
                        if (EBoxType.NORMAL.match(box.getBoxType())) {
                            if (box.getRule().get("playerNum") == 3 && box.getRule().get("playerMinNum") == null) {
                                bHas = true;
                            }
                        }
                    }
                    if (!bHas && info.robotDeskMax != 0) {
                        resp.ret = ErrorCode.API_NOT_THREE_DESK.getRet();
                        resp.msg = ErrorCode.API_NOT_THREE_DESK.getMsg();
                        break;
                    }
                    floor.setSetRobotDesk3Min(info.robotDeskMin);
                    floor.setSetRobotDesk3Max(info.robotDeskMax);
                    floor.setRandomTime3(info.randomTime);
                }
                
            }else if(info.type==3){
                {
                    boolean bHas = false;//是否有4人场的玩法桌
                    // 固定模式玩法桌
                    for (long id : gameId) {
                        Box box = iClub.getBox(id);
                        if (null == box) {
                            continue;
                        }
                        if (EBoxType.NORMAL.match(box.getBoxType())) {
                            if (box.getRule().get("playerNum") == 4 && box.getRule().get("playerMinNum") == null) {
                                bHas = true;
                            }
                        }
                    }
                    if (!bHas && info.robotDeskMax != 0) {
                        resp.ret = ErrorCode.API_NOT_THREE_DESK.getRet();
                        resp.msg = ErrorCode.API_NOT_THREE_DESK.getMsg();
                        break;
                    }
                    floor.setSetRobotDesk4Min(info.robotDeskMin);
                    floor.setSetRobotDesk4Max(info.robotDeskMax);
                    floor.setRandomTime4(info.randomTime);
                }
                
            }
            
         
            resp.clubUid = info.clubUid;
            resp.floorUid = info.floorUid;
            resp.type = info.type;
            resp.robotDeskMin = info.robotDeskMin;
            resp.robotDeskMax = info.robotDeskMax;
            resp.randomTime = info.randomTime;
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
