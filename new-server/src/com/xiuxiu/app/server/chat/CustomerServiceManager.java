package com.xiuxiu.app.server.chat;

import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerServiceManager {
    private static class CustomerServiceManagerHolder {
        private static CustomerServiceManager instance = new CustomerServiceManager();
    }

    public static CustomerServiceManager I = CustomerServiceManagerHolder.instance;

    private ConcurrentHashMap<Long, CustomerServiceInfo> services = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, CustomerServiceInfo> onlineServices = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, CustomerServiceInfo> customer2Service = new ConcurrentHashMap<>();
    private final long SERVICE_UID_BEGIN = 10001;   // 客服账号起始UID
    private final long SERVICE_UID_END = 11000;     // 客服账号截止UID
    private final long EXPIRE_SECONDS = 3600 * 24;

    private CustomerServiceManager() {
    }

    public boolean isCustomerService(long playerUid) {
        return playerUid >= SERVICE_UID_BEGIN && playerUid <= SERVICE_UID_END;
    }

    public void online(long playerUid) {
        if (!isCustomerService(playerUid)) {
            return;
        }
        CustomerServiceInfo info = this.services.get(playerUid);
        if (null == info) {
            info = new CustomerServiceInfo();
            info.setPlayerUid(playerUid);
            this.services.putIfAbsent(playerUid, info);
            info = this.services.get(playerUid);
        }
        info.setOnline(true);
        this.onlineServices.putIfAbsent(playerUid, info);
    }

    public void offline(long playerUid) {
        if (!isCustomerService(playerUid)) {
            return;
        }
        CustomerServiceInfo info = this.onlineServices.get(playerUid);
        if (null != info) {
            info.setOnline(false);
            this.onlineServices.remove(playerUid);
        }
    }

    public Player getCustomerService(Player player) {
        Player playerService = this.getCustomerService(player, true);
        if (playerService == null) {
            playerService = this.getCustomerService(player, false);
        }
        return playerService;
    }

    private Player getCustomerService(Player player, boolean needOnlineService) {
        CustomerServiceInfo service = this.customer2Service.get(player.getUid());
        if (null == service) {
            // 根据权重，随机分配一位在线客服
            ConcurrentHashMap<Long, CustomerServiceInfo> existsServices = needOnlineService ? this.onlineServices : this.services;
            List<CustomerServiceInfo> list = new ArrayList<>(existsServices.size());
            list.addAll(existsServices.values());
            if (list.isEmpty()) {
                return null;
            }
            int weight = 0;
            for (CustomerServiceInfo item: list) {
                weight += item.getWeight();
            }
            int val = RandomUtil.random(weight);
            for (CustomerServiceInfo item: list) {
                val -= item.getWeight();
                if (val <= 0) {
                    service = item;
                    break;
                }
            }
            if (null == service) {
                service = list.get(list.size() - 1);
            }
        }
        service.removeExpiredCustomers(EXPIRE_SECONDS);
        service.addCustomer(player.getUid());
        this.customer2Service.put(player.getUid(), service);
        return needOnlineService ? PlayerManager.I.getOnlinePlayer(service.getPlayerUid()) : PlayerManager.I.getPlayer(service.getPlayerUid());
    }

    private static class CustomerServiceInfo {
        private long playerUid;
        private volatile boolean online = true;
        private int weight = 10;
        private Map<Long, Long> customers = new ConcurrentHashMap<>();

        private long getPlayerUid() {
            return playerUid;
        }

        private void setPlayerUid(long playerUid) {
            this.playerUid = playerUid;
        }

        private boolean isOnline() {
            return online;
        }

        private void setOnline(boolean online) {
            this.online = online;
        }

        private int getWeight() {
            return this.weight;
        }

        private void setWeight(int weight) {
            this.weight = weight;
        }

        private void addCustomer(long playerUid) {
            this.customers.put(playerUid, System.currentTimeMillis() / 1000);
        }

        private void removeExpiredCustomers(long expireSeconds) {
            long now = System.currentTimeMillis() / 1000;
            Iterator<Map.Entry<Long, Long>> it = this.customers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, Long> entry = it.next();
                if (now - entry.getValue() > expireSeconds) {
                    it.remove();
                }
            }
        }
    }
}
