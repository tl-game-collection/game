package com.xiuxiu.app.server.services.api.club;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.KickOutClub;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfApplyLeave;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfUpdateMainClubInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubCloseStatus;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.AsyncTask;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther: yuyunfei
 * @date: 2020/1/5 14:03
 * @comment:踢出俱乐部
 */
public class KickOutClubHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        KickOutClub info = JsonUtil.fromJson(body, KickOutClub.class);
        Logs.API.warn("踢出俱乐部:%s", info);
        String sign = MD5Util.getMD5(info.clubUid, Config.APP_KEY);
        ErrorMsg resp = new ErrorMsg();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            IClub club = ClubManager.I.getClubByUid(info.clubUid);
            if (null == club){
                resp.ret = ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS.getMsg();
                break;
            }
            if (!club.checkIsLevelOneClub()){
                resp.ret = ErrorCode.CLUB_NOT_IS_MAINCLUB_OR_ONELEVEL.getRet();
                resp.msg = ErrorCode.CLUB_NOT_IS_MAINCLUB_OR_ONELEVEL.getMsg();
                break;
            }
            IClub lordClub = ClubManager.I.getClubByUid(club.getClubInfo().getParentUid());
            if (null == lordClub){
                resp.ret = ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS.getMsg();
                break;
            }
            if (lordClub.matchCloseStatus(EClubCloseStatus.OPEN)){
                resp.ret = ErrorCode.CLUB_NOT_CLOSE.getRet();
                resp.msg = ErrorCode.CLUB_NOT_CLOSE.getMsg();
                break;
            }
            ErrorCode err = lordClub.leaveMainClub(club);
            if (ErrorCode.OK != err) {
                resp.ret = err.getRet();
                resp.msg = err.getMsg();
                break;
            }
            AsyncTask.I.addTask(() -> {
                PCLIClubNtfApplyLeave leaveInfo = new PCLIClubNtfApplyLeave();
                leaveInfo.fromClubUid = club.getClubUid();
                leaveInfo.toClubUid = lordClub.getClubUid();
                lordClub.broadcast(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_NOTIFY, leaveInfo);
                club.broadcast(CommandId.CLI_NTF_CLUB_APPLY_LEAVE_NOTIFY, leaveInfo);
                if (club.getClubInfo().getChildUid().size() > 0) {
                    PCLIClubNtfUpdateMainClubInfo updateMainClubInfo = new PCLIClubNtfUpdateMainClubInfo();
                    updateMainClubInfo.mainClubInfo = club.getClubSingleInfoPCL(null);
                    List<Long> leaveChildClubUidList = new ArrayList<>();
                    club.fillDepthChildClubUidList(leaveChildClubUidList);
                    for (long childClubUid :leaveChildClubUidList){
                        IClub childClub = ClubManager.I.getClubByUid(childClubUid);
                        if (null != childClub) {
                            updateMainClubInfo.clubUid = childClubUid;
                            childClub.broadcast(CommandId.CLI_NTF_CLUB_UP_MAIN_CLUB_INFO, updateMainClubInfo);
                        }
                    }
                }
            });
            resp.ret =  ErrorCode.OK.getRet();
            resp.msg =  ErrorCode.OK.getMsg();
        } while (false);
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
