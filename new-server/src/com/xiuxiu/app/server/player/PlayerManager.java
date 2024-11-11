package com.xiuxiu.app.server.player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfKill;
import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.chat.CustomerServiceManager;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.mail.MailManager;
import com.xiuxiu.app.server.services.gateway.GatewaySessionContext;
import com.xiuxiu.app.server.statistics.constant.EMoneyExpendType;
import com.xiuxiu.core.net.Connection;
import com.xiuxiu.core.utils.AsyncTask;
import com.xiuxiu.core.utils.HttpUtil;
import com.xiuxiu.core.utils.StringUtil;

public class PlayerManager extends BaseManager {
    private static class PlayerManagerHolder {
        private static PlayerManager instance = new PlayerManager();
    }

    public static PlayerManager I = PlayerManagerHolder.instance;

    private ConcurrentHashMap<Long, Player> onlinePlayer = new ConcurrentHashMap<>();
    private static final int CACHE_SIZE = 10000;
    /** 离线玩家数据缓存器 */
    private LoadingCache<Long, Player> cache = CacheBuilder.newBuilder().maximumSize(CACHE_SIZE) // 最多存放数据大小
            .expireAfterWrite(3, TimeUnit.MINUTES) // 缓存3分钟
            .build(new CacheLoader<Long, Player>() {
                @Override
                public Player load(Long key) throws Exception {
                    Player player = DBManager.I.load(key, ETableType.TB_PLAYER);
                    if (null != player) {
                        player.init();
                    }
                    return player;
                }
            });

    private PlayerManager() {
    }

    public boolean isOnline(long uid) {
        return this.onlinePlayer.containsKey(uid);
    }

    public Player getOnlinePlayer(long uid) {
        return this.onlinePlayer.get(uid);
    }

    public ConcurrentHashMap<Long, Player> getOnlinePlayer() {
        return this.onlinePlayer;
    }

    public Player getPlayer(long uid) {
        if (uid < 0L) {
            return null;
        }
        Player player = this.onlinePlayer.get(uid);
        if (null != player) {
            return player;
        }
//        player = DBManager.I.load(uid, ETableType.TB_PLAYER);
//        if (null != player) {
//            player.init();
//        }
//        return player;
        try {
            return cache.get(uid);
        } catch (Exception e) {
//            e.printStackTrace();
            //log exception 
        }
        return null;
    }

    public Player createPlayer(Account account, int bizChannel) {
        Player player = new Player();
        player.setUid(account.getUid());
        player.setCreateTimestamp(System.currentTimeMillis());
        player.setBorn(946656000000L);
        player.setBizChannel(bizChannel);
        if (!StringUtil.isEmptyOrNull(account.getOtherPlatformToken())) {
            player.setSex(account.getSex());
            if (!StringUtil.isEmptyOrNull(account.getName())) {
                player.setName(account.getName());
            }
            if (!StringUtil.isEmptyOrNull(account.getIcon())) {
                player.setIcon(account.getIcon());
            }
            if (!StringUtil.isEmptyOrNull(account.getCity())) {
                player.setZone(account.getCity());
            }
        }
        if (StringUtil.isEmptyOrNull(player.getName())) {
            if (StringUtil.isEmptyOrNull(account.getPhone())) {
                player.setName("游客" + account.getUid());
            } else if (CustomerServiceManager.I.isCustomerService(account.getUid())) {
                player.setName("客服" + account.getUid());
            } else {
                Nickname nickname = DBManager.I.getNicknameDAO().getOne();
                if (nickname != null) {
                    nickname.state++;
                    DBManager.I.getNicknameDAO().save(nickname);
                    player.setName(nickname.state == 1 ? nickname.name : nickname.name + nickname.state);
                } else {
                    player.setName(account.getPhone());
                }
            }
        }
        // player.adjustName();
        // TODO 初始化钻石
        // player.addMoney(EMoneyType.DIAMOND, 50);
        player.addMoney(EMoneyType.DIAMOND, 5, player.getUid(), -1, EMoneyExpendType.NORMAL, -1);
        //MailManager.I.sendFirstMail(player);
        player.setDirty(true);
        player.save();
        final Player tempPlayer = player;
        AsyncTask.I.addTask(new Runnable() {
            @Override
            public void run() {
                Map<String, String> param = new HashMap<>();
                param.put("uid", tempPlayer.getUid() + "");
                param.put("channel", tempPlayer.getBizChannel() + "");
                param.put("nickname", tempPlayer.getName());
                HttpUtil.get(Config.STATISTICS_URL + "gatherRegister", param);
            }
        });
        return player;
    }

    public ErrorCode login(Connection conn, long uid) {
        if (!this.lock(uid)) {
            return ErrorCode.PLAYER_LOGINING;
        }
        try {
            GatewaySessionContext sessionContext = (GatewaySessionContext) conn.getSessionContext();
            Player player = this.onlinePlayer.get(uid);
            Logs.PLAYER_LOGIN.info("[LOGIN] playerUid:%d conn:%s oldConn:%s", uid, conn,
                    null == player ? "" : player.getConn());
            if (null != player) {
                Logs.PLAYER.info("%s 已经在线, 重登", player);
                // if (!player.getRemoteIp().equals(conn.getRemoteIp())) {
                // 异地登陆
                player.send(CommandId.CLI_NTF_PLAYER_REMOTE_LOGIN, null);
                // }
                player.logout(false);
            } else {
                player = this.getPlayer(uid);
            }
            if (null != player) {
                player.setConn(conn);

                Account account = AccountManager.I.getAccountByUid(uid);
                if (null != account && 0 != account.getState()) {
                    PCLIPlayerNtfKill kill = new PCLIPlayerNtfKill();
                    kill.reason = "被封禁";
                    player.send(CommandId.CLI_NTF_PLAYER_KILL, kill);
                    player.logout(true);
                    return ErrorCode.ACCOUNT_HAS_BEEN_BANNED;
                }

                sessionContext.setPlayerUid(player.getUid());
                this.onlinePlayer.putIfAbsent(uid, player);
                player.login();
                return ErrorCode.OK;
            }
            Account account = AccountManager.I.getAccountByUid(uid);
            if (null == account) {
                Logs.PLAYER.warn("玩家uid:%d 不存在", uid);
                return ErrorCode.PLAYER_NOT_EXISTS;
            }
            if (0 != account.getState()) {
                PCLIPlayerNtfKill kill = new PCLIPlayerNtfKill();
                kill.reason = "被封禁";
                // player.send(CommandId.CLI_NTF_PLAYER_KILL, kill);
                conn.close();
                return ErrorCode.ACCOUNT_HAS_BEEN_BANNED;
            }
            if (null == player) {
                player = this.createPlayer(account, -1);
            }
            player.setConn(conn);
            player.init();
            this.onlinePlayer.putIfAbsent(uid, player);
            sessionContext.setPlayerUid(player.getUid());
            Logs.PLAYER.warn("Connection:%s 开始登陆, hasOnline:%s", conn,
                    null == this.onlinePlayer.get(sessionContext.getPlayerUid()) ? "false" : "true");
            player.login();
            Logs.PLAYER.warn("Connection:%s 登陆完成, hasOnline:%s", conn,
                    null == this.onlinePlayer.get(sessionContext.getPlayerUid()) ? "false" : "true");
            return ErrorCode.OK;
        } finally {
            this.unlock(uid);
        }
    }

    public ErrorCode relogin(Connection conn, long uid) {
        if (!this.lock(uid)) {
            return ErrorCode.PLAYER_LOGINING;
        }
        try {
            GatewaySessionContext sessionContext = (GatewaySessionContext) conn.getSessionContext();
            Player player = this.onlinePlayer.get(uid);
            Logs.PLAYER_LOGIN.info("[RELOGIN] playerUid:%d conn:%s oldConn:%s", uid, conn,
                    null == player ? "" : player.getConn());
            if (null != player) {
                Logs.PLAYER.info("%s 已经在线, 重登", player);
                player.logout(false);
            } else {
                player = this.getPlayer(uid);
            }
            if (null == player) {
                Logs.PLAYER.warn("玩家uid:%d 不存在", uid);
                return ErrorCode.PLAYER_NOT_EXISTS;
            }

            player.setConn(conn);

            Account account = AccountManager.I.getAccountByUid(uid);
            if (null != account && 0 != account.getState()) {
                PCLIPlayerNtfKill kill = new PCLIPlayerNtfKill();
                kill.reason = "被封禁";
                player.send(CommandId.CLI_NTF_PLAYER_KILL, kill);
                player.logout(true);
                return ErrorCode.ACCOUNT_HAS_BEEN_BANNED;
            }

            player.init();
            sessionContext.setPlayerUid(player.getUid());
            this.onlinePlayer.putIfAbsent(uid, player);
            Logs.PLAYER.warn("Connection:%s 开始重登, hasOnline:%s", conn,
                    null == this.onlinePlayer.get(sessionContext.getPlayerUid()) ? "false" : "true");
            player.relogin();
            Logs.PLAYER.warn("Connection:%s 重登完成, hasOnline:%s", conn,
                    null == this.onlinePlayer.get(sessionContext.getPlayerUid()) ? "false" : "true");
            return ErrorCode.OK;
        } finally {
            this.unlock(uid);
        }
    }

    public boolean logout(Connection conn) {
        GatewaySessionContext sessionContext = (GatewaySessionContext) conn.getSessionContext();
        Player player = this.onlinePlayer.get(sessionContext.getPlayerUid());
        if (null == player) {
            Logs.PLAYER.warn("Connection:%s 没有登陆", conn);
            return false;
        }
        Logs.PLAYER_LOGOUT.info("[LOGOUT] playerUid:%d conn:%s", player.getUid(), player.getConn());
        // while (!this.lock(player.getUid())) {
        // Thread.yield();
        // }
        try {
            if (conn == player.getConn()) {
                Logs.PLAYER.warn("Connection:%s 开始登出, hasOnline:%s", conn,
                        null == this.onlinePlayer.get(sessionContext.getPlayerUid()) ? "false" : "true");
                player.logout(true);
                this.onlinePlayer.remove(sessionContext.getPlayerUid());
                Logs.PLAYER.warn("Connection:%s 登出完成, hasOnline:%s", conn,
                        null == this.onlinePlayer.get(sessionContext.getPlayerUid()) ? "false" : "true");
            }
        } finally {
           // this.unlock(player.getUid());
        }

        return true;
    }

    public void broadAll(int commandId, Object message) {
        Iterator<Map.Entry<Long, Player>> it = this.onlinePlayer.entrySet().iterator();
        while (it.hasNext()) {
            Player player = it.next().getValue();
            player.send(commandId, message);
        }
    }

    public int countOfOnlinePlayers() {
        return this.onlinePlayer.size();
    }

    @Override
    public int save() {
        int cnt = 0;
        Iterator<Map.Entry<Long, Player>> it = this.onlinePlayer.entrySet().iterator();
        while (it.hasNext()) {
            Player player = it.next().getValue();
            if (player.save()) {
                ++cnt;
            }
        }
        Logs.ONLINE.info("当前在线人数:%d", this.onlinePlayer.size());
        return cnt;
    }

    @Override
    public int shutdown() {
        int cnt = 0;
        try {
            Iterator<Map.Entry<Long, Player>> it = this.onlinePlayer.entrySet().iterator();
            while (it.hasNext()) {
                Player player = it.next().getValue();
                player.logout(true);
                ++cnt;
            }
        } catch (Throwable e) {
            Logs.CORE.error(e);
        }

        return cnt;
    }
}
