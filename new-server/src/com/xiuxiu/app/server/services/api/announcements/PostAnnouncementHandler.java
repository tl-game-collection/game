package com.xiuxiu.app.server.services.api.announcements;

import com.xiuxiu.app.server.services.api.old.handler.BaseAdminHttpHandler;
import com.xiuxiu.app.server.system.Announcement;
import com.xiuxiu.app.server.system.AnnouncementManager;
import com.xiuxiu.core.net.protocol.ErrorCode;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.JsonUtil;

/**
 * 发布公告
 * {
 *     "id": 1,
 *     "content": "公告内容",
 *     "announceAt": 1531091291,
 *     "expireAt": 1531091291,
 *     "repeatInterval": 60
 * }
 */
public class PostAnnouncementHandler extends BaseAdminHttpHandler {
    @Override
    public ErrorMsg doHandle(String data) {
        Announcement announcement = JsonUtil.fromJson(data, Announcement.class);
        if (null == announcement
                || announcement.getAnnounceAt() > announcement.getExpireAt() // StringUtil.isEmptyOrNull(announcement.getContent())
                || announcement.getExpireAt() <= System.currentTimeMillis() / 1000) {
            return new ErrorMsg(ErrorCode.INVALID_DATA);
        }
        AnnouncementManager.I.scheduleOne(announcement);
        return new ErrorMsg(ErrorCode.OK);
    }
}
