package com.xiuxiu.app.server.system;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.system.PCLISystemNtfAnnouncement;
import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.utils.TimerHolder;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 公告管理
 */
public class AnnouncementManager extends BaseManager {
    private static class AnnouncementManagerHolder {
        private static AnnouncementManager instance = new AnnouncementManager();
    }

    public static AnnouncementManager I = AnnouncementManagerHolder.instance;

    private Map<Long, Announcement> newAnnouncements = new ConcurrentHashMap<>();
    private Map<Long, Announcement> postingAnnouncements = new ConcurrentHashMap<>();
    
    public void clear() {
        this.newAnnouncements.clear();
        this.postingAnnouncements.clear();
    }

    public void scheduleOne(Announcement announcement) {
        long currentSecs = System.currentTimeMillis() / 1000;
        if (announcement.getExpireAt() < currentSecs) {
            return;
        }
        if (this.newAnnouncements.containsKey(announcement.getId())
                || this.postingAnnouncements.containsKey(announcement.getId())) {
            return;
        }
        this.newAnnouncements.clear();
        this.newAnnouncements.put(announcement.getId(), announcement);
        long waitSeconds = Math.max(announcement.getAnnounceAt() - currentSecs, 0);
        TimerHolder.getTimer().newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) {
                AnnouncementManager.I.postAnnouncementsIfNeeded();
            }
        }, waitSeconds, TimeUnit.SECONDS);
    }

    public List<Announcement> getAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        announcements.addAll(postingAnnouncements.values());
        announcements.addAll(newAnnouncements.values());
        return announcements;
    }

    public void postAnnouncementsIfNeeded(Player player) {
        long currentSecs = System.currentTimeMillis() / 1000;
        Iterator<Announcement> it = this.postingAnnouncements.values().iterator();
        while (it.hasNext()) {
            Announcement announcement = it.next();
            if (announcement.getExpireAt() < currentSecs) {
                it.remove();
                continue;
            }
            // TODO If there are multiple announcements, there can be optimized of list delivery.
            PCLISystemNtfAnnouncement msg = this.createMessage(announcement);
            player.send(CommandId.CLI_NTF_SYSTEM_ANNOUNCEMENT, msg);
        }
    }

    private void postAnnouncementsIfNeeded() {
        Logs.CHAT.debug("time to post announcements");
        long currentSecs = System.currentTimeMillis() / 1000;
        Iterator<Announcement> it = this.newAnnouncements.values().iterator();
        while (it.hasNext()) {
            Announcement announcement = it.next();
            if (announcement.getExpireAt() < currentSecs) {
                it.remove();
                continue;
            }
            if (announcement.getAnnounceAt() <= currentSecs) {
                it.remove();
                this.postingAnnouncements.clear();
                this.postingAnnouncements.put(announcement.getId(), announcement);

                PCLISystemNtfAnnouncement msg = this.createMessage(announcement);
                PlayerManager.I.broadAll(CommandId.CLI_NTF_SYSTEM_ANNOUNCEMENT, msg);
            }
        }
    }

    private PCLISystemNtfAnnouncement createMessage(Announcement announcement) {
        PCLISystemNtfAnnouncement msg = new PCLISystemNtfAnnouncement();
        msg.uid = announcement.getId();
        msg.content = announcement.getContent();
        msg.repeatInterval = (int) announcement.getRepeatInterval();
        long time = announcement.getExpireAt() - System.currentTimeMillis() / 1000;
        msg.repeatTimes = (int) (time / announcement.getRepeatInterval());
        return msg;
    }
    
    @Override
    public int save() {
        return 0;
    }

    @Override
    public int shutdown() {
        return 0;
    }
}
