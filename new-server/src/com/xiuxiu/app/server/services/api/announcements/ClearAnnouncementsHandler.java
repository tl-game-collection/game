package com.xiuxiu.app.server.services.api.announcements;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.system.PCLISystemNtfAnnouncement;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.services.api.old.handler.BaseAdminHttpHandler;
import com.xiuxiu.app.server.system.AnnouncementManager;
import com.xiuxiu.core.net.protocol.ErrorMsg;

/**
 * 清空公告
 * 
 * @author Administrator
 *
 */
public class ClearAnnouncementsHandler extends BaseAdminHttpHandler {

    @Override
    public ErrorMsg doHandle(String data) {
        ErrorMsg response = new ErrorMsg(ErrorCode.OK);
        AnnouncementManager.I.clear();

        PCLISystemNtfAnnouncement msg = new PCLISystemNtfAnnouncement();
        msg.uid = -1;
        msg.content = "";
        msg.repeatInterval = 0;
        msg.repeatTimes = 0;
        PlayerManager.I.broadAll(CommandId.CLI_NTF_SYSTEM_ANNOUNCEMENT, msg);
        return response;
    }
}
