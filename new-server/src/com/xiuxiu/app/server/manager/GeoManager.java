package com.xiuxiu.app.server.manager;

import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.Task;
import com.xiuxiu.core.utils.AsyncTask;
import org.redisson.api.GeoUnit;
import org.redisson.api.RGeo;
import org.redisson.api.RedissonClient;

import java.util.Collections;
import java.util.List;

public class GeoManager extends BaseManager {
    private static class GeoManagerHolder {
        private static GeoManager instance = new GeoManager();
    }

    public static GeoManager I = GeoManagerHolder.instance;

    private static final String GEO_KEY = "xx_geo_player_";
    private RedissonClient redisClient;
    private RGeo<GeoPlayer> geo;

    private GeoManager() {
    }

    public void init(RedissonClient client) {
        this.redisClient = client;
        this.geo = this.redisClient.getGeo(GEO_KEY);
    }

    public boolean add(final Player player, double newLat, double newLng) {
        if (!this.lock(player.getUid())) {
            Logs.PLAYER.warn("%s 正在添加到geo信息库中", player);
            return false;
        }
        AsyncTask.I.addTask(new Task() {
            @Override
            public void run() {
                try {
                    geo.add(newLng, newLat, new GeoPlayer(player.getUid(), player.getSex()));
                    player.setLat(newLat);
                    player.setLng(newLng);
                    player.setDirty(true);
                } catch (Exception e) {
                    Logs.PLAYER.error("添加geo错误", e);
                }
                unlock(player.getUid());
            }
        });
        return true;
    }

    public List<GeoPlayer> getRadius(Player player, int m) {
        if (!this.lock(player.getUid())) {
            Logs.CORE.warn("%s 正在获取geo信息库 m: %d", player, m);
            return Collections.EMPTY_LIST;
        }
        try {
            return this.geo.radius(player.getLng(), player.getLat(), m * 1000, GeoUnit.METERS);
        } finally {
            this.unlock(player.getUid());
        }
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
