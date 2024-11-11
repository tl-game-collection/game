package com.xiuxiu.app.server.club.helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClubHelperManager {
    private static class ClubHelperManagerHolder {
        private static ClubHelperManager instance = new ClubHelperManager();
    }

    public static ClubHelperManager I = ClubHelperManagerHolder.instance;

    private Map<Long, Boolean> inviteSettings = new ConcurrentHashMap<Long, Boolean>();

    /**
     * 是否允许邀请,默认允许
     * 
     * @param playerUid
     * @return
     */
    public boolean isAllowInvite(long playerUid) {
        if (inviteSettings.containsKey(playerUid)) {
            return inviteSettings.get(playerUid);
        }
        return Boolean.TRUE;
    }

    public void resetAllowInvite(long playerUid, boolean status) {
        inviteSettings.put(playerUid, status);
    }

    public void zero() {
        inviteSettings.clear();
    }

}
