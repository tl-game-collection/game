package com.xiuxiu.app.server;

import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.core.ds.ConcurrentHashSet;
import com.xiuxiu.core.utils.AsyncTask;

public abstract class BaseManager {
    protected ConcurrentHashSet<Long> lock = new ConcurrentHashSet<>();

    public boolean lock(long playerUid) {
        return this.lock.add(playerUid);
    }

    public boolean unlock(long playerUid) {
        return this.lock.remove(playerUid);
    }

    public abstract int save();

    public abstract int shutdown();
    
    /**
     * 通知消息
     * 
     * @param club
     * @param commandId
     * @param message
     */
    protected void notice(IClub club, int commandId, Object message) {
        AsyncTask.I.addTask(() -> {
            club.broadcast(commandId, message);
        });
    }
    

    /**
     * 通知消息
     * 
     * @param club
     * @param commandId
     * @param message
     * @param playerUid
     */
    protected void notice(IClub club, int commandId, Object message, long playerUid) {
        AsyncTask.I.addTask(() -> {
            club.broadcast(commandId, message, playerUid);
        });
    }
}
