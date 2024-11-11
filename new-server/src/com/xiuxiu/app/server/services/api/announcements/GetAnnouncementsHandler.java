package com.xiuxiu.app.server.services.api.announcements;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.services.api.old.handler.BaseAdminHttpHandler;
import com.xiuxiu.app.server.system.Announcement;
import com.xiuxiu.app.server.system.AnnouncementManager;
import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.List;

public class GetAnnouncementsHandler extends BaseAdminHttpHandler {
    @Override
    public ErrorMsg doHandle(String data) {
        Response response = new Response(ErrorCode.OK);
        response.setEntities(AnnouncementManager.I.getAnnouncements());
        return response;
    }

    private class Response extends ErrorMsg {
        long timestamp;
        List<Announcement> entities;

        Response(ErrorCode err) {
            super(err);
            this.timestamp = System.currentTimeMillis() / 1000;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public List<Announcement> getEntities() {
            return entities;
        }

        public void setEntities(List<Announcement> entities) {
            this.entities = entities;
        }
    }
}
