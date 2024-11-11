package com.xiuxiu.app.server;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.chat.MailBoxManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.DBReplaceManager;
import com.xiuxiu.app.server.club.activity.ClubActivityManager;
import com.xiuxiu.app.server.club.helper.ClubHelperManager;
import com.xiuxiu.app.server.codec.MyJsonJacksonCodec;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.floor.FloorManager;
import com.xiuxiu.app.server.forbid.ForbidManager;
import com.xiuxiu.app.server.manager.GeoManager;
import com.xiuxiu.app.server.manager.VersionManager;
import com.xiuxiu.app.server.order.UpDownGoldTreasurerManager;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.process.*;
import com.xiuxiu.app.server.rank.NewRankManager;
import com.xiuxiu.app.server.room.CardLibraryManager;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.services.account.AccountServer;
import com.xiuxiu.app.server.services.api.ApiServer;
import com.xiuxiu.app.server.services.gateway.GatewayServer;
import com.xiuxiu.app.server.services.gateway.stat.GateStat;
import com.xiuxiu.app.server.statistics.DownLineGameManager;
import com.xiuxiu.app.server.statistics.StatManager;
import com.xiuxiu.app.server.table.TableManager;
import com.xiuxiu.core.boot.BootChain;
import com.xiuxiu.core.boot.BootJob;
import com.xiuxiu.core.utils.*;
import com.xiuxiu.sms.SMSManager;
import com.xiuxiu.wechat.WeChat;
import com.xiuxiu.xpush.XPushManager;

import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.apache.commons.cli.*;
import org.redisson.api.RedissonClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

public class Main {
    private static class MainHolder {
        private static Main instance = new Main();
    }

    public static Main I = MainHolder.instance;

    private AccountServer accountServer;
    private ApiServer apiServer;
    private GatewayServer gatewayServer;
    private RedissonClient redisson;
    private AsyncMessageProcess asyncMessageProcess;
    private LoginMessageProcess loginMessageProcess;
    private PlayerMessageProcess playerMessageProcess;
    private RoomMessageProcess roomMessageProcess;
    private ChatMessageProcess chatMessageProcess;
    private ClubMessageProcess clubMessageProcess;
    private BoxMessageProcess boxMessageProcess;
    private BootChain lunch = BootChain.chain();

    private FileOutputStream fileOutputStream = null;
    private PrintStream ps = null;

    private volatile boolean stop = true;

    private Main() {
    }

    public static void main(String[] args) throws InterruptedException, ParseException {
        Options options = new Options();
        options.addOption(null, "server-version", true, "app server version");
        options.addOption(null, "ip", true, "gateway server ip");
        options.addOption(null, "host", true, "gateway server host");
        options.addOption(null, "port", true, "gateway server port");
        options.addOption(null, "login-ip", true, "login server ip");
        options.addOption(null, "login-port", true, "login server port");
        options.addOption(null, "api-ip", true, "api server ip");
        options.addOption(null, "api-port", true, "api server port");
        options.addOption(null, "app-key", true, "app key");
        options.addOption(null, "agency-url", true, "agency url");
        options.addOption(null, "file-upload-url", true, "file upload server url");
        options.addOption(null, "file-download-url", true, "file download server url");
        options.addOption(null, "statistics-url", true, "statistics url");
        options.addOption(null, "record-path", true, "record save path");
        options.addOption(null, "redis", true, "redis server");
        options.addOption(null, "wechat-appid", true, "wechat appid");
        options.addOption(null, "wechat-secret", true, "wechat secret");
        options.addOption(null, "jpush-appkey", true, "jspush app key");
        options.addOption(null, "jpush-secret", true, "jpush master secret");
        options.addOption(null, "amap-key", true, "amap app key");
        options.addOption(null, "amap-regeo-url", true, "amap regeo url");
        options.addOption(null, "key-filter", true, "key-filter file path");
        options.addOption(null, "sms-type", true, "sms type, ps: ali, huyi, fgcs");
        options.addOption(null, "sms-key-id", true, "sms access key id");
        options.addOption(null, "sms-key-secret", true, "sms access key secret");
        options.addOption(null, "sms-sign-name", true, "sms sign name");
        options.addOption(null, "sms-template-code", true, "sms template code");
        options.addOption(null, "switch-privilege-verify", true, "privilege verify switch");
        options.addOption(null, "prob-fgf", true, "fgf prob");
        options.addOption(null, "develop", true, "develop true or false");
        options.addOption("h", "help", false, "help");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption('h')) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("xiuxiu game db server", options);
            return;
        }

        if (commandLine.hasOption("ip")) {
        	//网关服务器的IP
            Config.GATEWAY_SERVER_IP = commandLine.getOptionValue("ip");
        }
        if (commandLine.hasOption("host")) {
        	//网关服务器的HOST
            Config.GATEWAY_SERVER_HOST = commandLine.getOptionValue("host");
        }
        if (commandLine.hasOption("port")) {
        	//网关服务器的PORT
            Config.GATEWAY_SERVER_PORT = Integer.parseInt(commandLine.getOptionValue("port"));
        }
        if (commandLine.hasOption("login-ip")) {
        	//登录服务器主机
            Config.LOGIN_SERVER_HOST = commandLine.getOptionValue("login-ip");
        }
        if (commandLine.hasOption("login-port")) {
        	//登录服务器PORT
            Config.LOGIN_SERVER_PORT = Integer.parseInt(commandLine.getOptionValue("login-port"));
        }
        if (commandLine.hasOption("api-ip")) {
        	//API服务器主机
            Config.API_SERVER_HOST = commandLine.getOptionValue("api-ip");
        }
        if (commandLine.hasOption("api-port")) {
        	//API服务器PORT
            Config.API_SERVER_PORT = Integer.parseInt(commandLine.getOptionValue("api-port"));
        }
        if (commandLine.hasOption("app-key")) {
        	//APP KEY
            Config.APP_KEY = commandLine.getOptionValue("app-key");
        }
        if (commandLine.hasOption("agency-url")) {
        	//公司网址
            Config.AGENCY_URL = commandLine.getOptionValue("agency-url");
        }
        if (commandLine.hasOption("file-upload-url")) {
        	//文件上传服务器URL
            Config.FILE_UPLOAD_SERVER_URL = commandLine.getOptionValue("file-upload-url");
        }
        if (commandLine.hasOption("file-download-url")) {
        	//文件下载服务器URL
            Config.FILE_DOWNLOAD_SERVER_URL = commandLine.getOptionValue("file-download-url");
        }
        if (commandLine.hasOption("statistics-url")) {
        	//统计数据的URL
            Config.STATISTICS_URL = commandLine.getOptionValue("statistics-url");
        }
        if (commandLine.hasOption("record-path")) {
        	//记录路径
            Config.RECORD_PATH = commandLine.getOptionValue("record-path");
        }
        if (commandLine.hasOption("redis")) {
        	//redis路径
            Config.REDIS_URL = commandLine.getOptionValue("redis");
        }
        if (commandLine.hasOption("wechat-appid")) {
        	//微信应用程序ID
            Config.WECHAT_APP_ID = commandLine.getOptionValue("wechat-appid").split(",");
        }
        if (commandLine.hasOption("wechat-secret")) {
        	//微信软件的秘密？？
            Config.WECHAT_APP_SECRET = commandLine.getOptionValue("wechat-secret").split(",");
        }
        if (commandLine.hasOption("jpush-appkey")) {
        	//JPUSH(推送)应用的关键
            Config.JPUSH_APP_KEY = commandLine.getOptionValue("jpush-appkey");
        }
        if (commandLine.hasOption("jpush-secret")) {
        	//JPUSH大师秘密？？
            Config.JPUSH_MASTER_SECRET = commandLine.getOptionValue("jpush-secret");
        }
        if (commandLine.hasOption("amap-key")) {
        	//amap-key？？
            Config.AMAP_KEY = commandLine.getOptionValue("amap-key");
        }
        if (commandLine.hasOption("amap-regeo-url")) {
        	//amap-regeo-url？？
            Config.AMAP_REGEO_URL = commandLine.getOptionValue("amap-regeo-url");
        }
        if (commandLine.hasOption("key-filter")) {
        	//关键过滤路径
            Config.KEY_FILTER_PATH = commandLine.getOptionValue("key-filter");
        }
        if (commandLine.hasOption("sms-type")) {
        	//短信配置type
            Config.SMS_CONFIG.type = commandLine.getOptionValue("sms-type");
        }
        if (commandLine.hasOption("sms-key-id")) {
        	//短信配置KeyId
            Config.SMS_CONFIG.accessKeyId = commandLine.getOptionValue("sms-key-id");
        }
        if (commandLine.hasOption("sms-key-secret")) {
        	//短信配置KeySecret
            Config.SMS_CONFIG.accessKeySecret = commandLine.getOptionValue("sms-key-secret");
        }
        if (commandLine.hasOption("sms-sign-name")) {
        	//短信配置signName
            Config.SMS_CONFIG.signName = commandLine.getOptionValue("sms-sign-name");
        }
        if (commandLine.hasOption("sms-template-code")) {
        	//短信配置templateCode
            Config.SMS_CONFIG.templateCode = commandLine.getOptionValue("sms-template-code");
        }
        if (commandLine.hasOption("switch-privilege-verify")) {
        	//球员权限验证
            Switch.PLAYER_PRIVILEGE_VERIFY = Long.valueOf(commandLine.getOptionValue("switch-privilege-verify"), 16);
        }
        if (commandLine.hasOption("prob-fgf")) {
        	//prob-fgf？？
            ProbConfig.setPropFgf(commandLine.getOptionValue("prob-fgf"));
        }
        if (commandLine.hasOption("develop")) {
        	//开发
            Config.DEVELOP = Boolean.valueOf(commandLine.getOptionValue("develop", "false"));
        }
        OSUtil.addShutdownHook(new Runnable() {
            @Override
            public void run() {
                Main.I.stop();
            }
        });
        //启动
        Main.I.start();
        while (!Main.I.stop) {
            Thread.sleep(1000);
        }
    }

    public void init() {
    	//异步消息处理
        this.asyncMessageProcess = new AsyncMessageProcess();
        //登录消息处理
        this.loginMessageProcess = new LoginMessageProcess();
        //聊天信息处理
        this.chatMessageProcess = new ChatMessageProcess();
        //俱乐部消息处理
        this.clubMessageProcess = new ClubMessageProcess();
        //玩家线程数
        PlayerProcessThread[] playerProcessThreads = new PlayerProcessThread[Config.PLAYER_PROCESS_THREAD_NUM];
        for (int i = 0, len = playerProcessThreads.length; i < len; ++i) {
            playerProcessThreads[i] = new PlayerProcessThread(i, len);
            playerProcessThreads[i].start();
        }
        //玩家消息处理
        this.playerMessageProcess = new PlayerMessageProcess(playerProcessThreads);
        RoomProcessThread[] roomProcessThreads = new RoomProcessThread[Config.ROOM_PROCESS_THREAD_NUM];
        for (int i = 0, len = roomProcessThreads.length; i < len; ++i) {
            roomProcessThreads[i] = new RoomProcessThread(i, len);
            roomProcessThreads[i].start();
        }
        //房间消息处理
        this.roomMessageProcess = new RoomMessageProcess(roomProcessThreads);
        
        //盒子进程的线程
        BoxProcessThread[] boxProcessThreads = new BoxProcessThread[Config.ARENA_PROCESS_THREAD_NUM];
        for (int i = 0, len = boxProcessThreads.length; i < len; ++i) {
            boxProcessThreads[i] = new BoxProcessThread(i, len);
            boxProcessThreads[i].start();
        }
        //盒子消息处理
        this.boxMessageProcess = new BoxMessageProcess(boxProcessThreads);

        //api服务器
        this.apiServer = new ApiServer(Config.API_SERVER_HOST, Config.API_SERVER_PORT);
        //账号服务器
        this.accountServer = new AccountServer(Config.LOGIN_SERVER_HOST, Config.LOGIN_SERVER_PORT);
        //网关服务器
        this.gatewayServer = new GatewayServer(Config.GATEWAY_SERVER_IP, Config.GATEWAY_SERVER_PORT);
        // TODO 需要集群
        org.redisson.config.Config config = new org.redisson.config.Config();
        config.useSingleServer().setAddress(Config.REDIS_URL).setPassword("redis123$%^").setDatabase(1);
        config.setCodec(new MyJsonJacksonCodec());
        //redis（PS:缓存）
        this.redisson = Redisson.create(config);
        //((Redisson) this.redisson).setKeyPreFix("duyq");

        //数据库初始化
        DBManager.I.init(this.redisson);
        //uid初始化
        UIDManager.I.init(this.redisson);
        //短信初始化
        SMSManager.I.init(this.redisson, Config.SMS_CONFIG);
        //？？
        GeoManager.I.init(this.redisson);
        //房间初始化
        RoomManager.I.init();
        //牌库初始化
        CardLibraryManager.I.init();
        //XPushManager.I.init(Config.JPUSH_APP_KEY, Config.JPUS6H_MASTER_SECRET, true);
        for (int i = 0, len = Config.WECHAT_APP_ID.length; i < len; ++i) {
            WeChat.I.add(i, Config.WECHAT_APP_ID[i], Config.WECHAT_APP_SECRET[i]);
        }
        //邮箱初始化
        MailBoxManager.I.init();

        //机器人设置为离线状态
        DBManager.I.getPlayerDao().resetAllRobotLoginOutTime(System.currentTimeMillis());

        //零点检测
        this.checkZero();
        //1分钟检测
        this.checkOne();
        //保存检测
        this.checkSave();
        //统计检测
        this.checkSyncForStats();

        this.lunch.next(new BootJob() {
            @Override
            protected void start() {
                VersionManager.I.init();
                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
                Logs.CORE.info("开始停止redis");
                long begin = System.currentTimeMillis();
                redisson.shutdown();
                Logs.CORE.info("停止redis完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                VersionManager.I.shutdown();
                Logs.CORE.info("关服完毕");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).next(new BootJob() {
            @Override
            protected void start() {
                try {
                    redirectIOStreams(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Logs.CORE.info("开始加载静态表数据");
                long begin = System.currentTimeMillis();
                TableManager.I.loadAll();
                Logs.CORE.info("开始加载静态表数据, 耗时:%d ms", (System.currentTimeMillis() - begin));
                Logs.CORE.info("开始加载UID");
                begin = System.currentTimeMillis();
                UIDManager.I.loadAll();
                Logs.CORE.info("加载UID完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                Logs.CORE.info("开始加载账号UID");
                begin = System.currentTimeMillis();
                AccountManager.I.init();
                Logs.CORE.info("加载账号UID完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                Logs.CORE.info("开始加载亲友圈数据");
                begin = System.currentTimeMillis();
                ClubManager.I.init();
                Logs.CORE.info("加载亲友圈数据完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                Logs.CORE.info("开始加载楼层数据");
                begin = System.currentTimeMillis();
                FloorManager.I.loadAll();
                Logs.CORE.info("加载楼层数据完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                Logs.CORE.info("开始加载包厢数据");
                begin = System.currentTimeMillis();
                BoxManager.I.loadAll();
                Logs.CORE.info("加载包厢数据完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                Logs.CORE.info("开始加载亲友圈活动数据");
                begin = System.currentTimeMillis();
                ClubActivityManager.I.init();
                Logs.CORE.info("加载亲友活动圈数据完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                begin = System.currentTimeMillis();
                NewRankManager.I.init();
                Logs.CORE.info("初始化排行榜数据完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                Logs.CORE.info("开始初始化关键字过滤数据");
                begin = System.currentTimeMillis();
                KeyFilterManager.I.init();
                Logs.CORE.info("初始化关键字数据完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                Logs.CORE.info("开始加载防作弊数据");
                begin = System.currentTimeMillis();
                ForbidManager.I.loadAll();
                Logs.CORE.info("加载防作弊数据完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                Logs.CORE.info("开始加载下级统计数据");
                begin = System.currentTimeMillis();
                DownLineGameManager.I.init();
                Logs.CORE.info("加载下级统计数据完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                Logs.CORE.info("开始加载下分订单数据");
                begin = System.currentTimeMillis();
                UpDownGoldTreasurerManager.I.init();
                Logs.CORE.info("加载下分订单数据完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                Logs.CORE.info("开始加载vip数据");
                begin = System.currentTimeMillis();
                Config.initVipList();
                Logs.CORE.info("加载vip数据完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                //DBReplaceManager.I.init(); // 修改一次后期删除

                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
            }
        }).next(new BootJob() {
            @Override
            protected void start() {
                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
                Logs.CORE.info("开始保存排行榜数据");
                long begin = System.currentTimeMillis();
                int cnt = NewRankManager.I.save();
                Logs.CORE.info("保存排行榜数据完成(%d), 耗时:%d ms", cnt, (System.currentTimeMillis() - begin));
            }
        }).next(new BootJob() {
            @Override
            protected void start() {
                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
                Logs.CORE.info("开始保存下级统计数据");
                long begin = System.currentTimeMillis();
                DownLineGameManager.I.save();
                Logs.CORE.info("保存保存下级统计数据完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
            }
        }).next(new BootJob() {
            @Override
            protected void start() {
                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
                Logs.CORE.info("开始踢出并保存玩家数据");
                long begin = System.currentTimeMillis();
                int cnt = PlayerManager.I.shutdown();
                Logs.CORE.info("提出并保持玩家数据完成(%d), 耗时:%d ms", cnt, (System.currentTimeMillis() - begin));
            }
        }).next(new BootJob() {
            @Override
            protected void start() {
                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
                Logs.CORE.info("开始卸载邮箱服务");
                long begin = System.currentTimeMillis();
                MailBoxManager.I.shutdown();
                Logs.CORE.info("卸载邮箱服务完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
            }
        }).next(new BootJob() {
            @Override
            protected void start() {
                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
                Logs.CORE.info("开始保存房间数据");
                long begin = System.currentTimeMillis();
                int cnt = RoomManager.I.save();
                Logs.CORE.info("保持房间数据完成(%d), 耗时:%d ms", cnt, (System.currentTimeMillis() - begin));
            }
        }).next(new BootJob() {
            @Override
            protected void start() {
                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
                Logs.CORE.info("开始保存群组数据");
                long begin = System.currentTimeMillis();
                int cnt = ClubManager.I.save();
                Logs.CORE.info("保持群组数据完成(%d), 耗时:%d ms", cnt, (System.currentTimeMillis() - begin));
            }
        }).next(new BootJob() {
            @Override
            protected void start() {
                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
                Logs.CORE.info("开始解散房间");
                long begin = System.currentTimeMillis();
                int cnt = RoomManager.I.shutdown();
                Logs.CORE.info("解散房间完成(%d), 耗时:%d ms", cnt, (System.currentTimeMillis() - begin));
            }
        }).next(new BootJob() {
            @Override
            protected void start() {
                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
                Logs.CORE.info("开始保存db数据");
                long begin = System.currentTimeMillis();
                DBManager.I.shutdown();
                Logs.CORE.info("保持db数据完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
            }
        }).next(new BootJob() {
            @Override
            protected void start() {
                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
                Logs.CORE.info("开始停止ExecutorService");
                long begin = System.currentTimeMillis();
                try {
                    loginMessageProcess.shutdown();
                    asyncMessageProcess.shutdown();
                    clubMessageProcess.shutdown();
                    chatMessageProcess.shutdown();
                    playerMessageProcess.shutdown();
                    boxMessageProcess.shutdown();
                    roomMessageProcess.shutdown();
                    TimerHolder.stop();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                Logs.CORE.info("停止ExecutorService完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
            }
        }).next(new BootJob() {
            @Override
            protected void start() {
                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
                Logs.CORE.info("开始卸载推送服务");
                long begin = System.currentTimeMillis();
               // XPushManager.I.shutdown();
                Logs.CORE.info("卸载推送服务完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
            }
        }).next(new BootJob() {
            @Override
            protected void start() {
                Logs.CORE.info("开始启动Api服务");
                long begin = System.currentTimeMillis();
                apiServer.start();
                Logs.CORE.info("启动Api服务完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
                Logs.CORE.info("开始停止Api服务");
                long begin = System.currentTimeMillis();
                apiServer.stop();
                Logs.CORE.info("停止Api服务完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
            }
        }).next(new BootJob() {
            @Override
            protected void start() {
                Logs.CORE.info("开始启动Account服务");
                long begin = System.currentTimeMillis();
                accountServer.start();
                Logs.CORE.info("启动Account服务完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
                Logs.CORE.info("开始停止Account服务");
                long begin = System.currentTimeMillis();
                accountServer.stop();
                Logs.CORE.info("停止Account服务完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
            }
        }).next(new BootJob() {
            @Override
            protected void start() {
                Logs.CORE.info("开始启动Gateway服务");
                long begin = System.currentTimeMillis();
                gatewayServer.start();
                Logs.CORE.info("启动Gateway服务完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
                VersionManager.I.setupSucc();
                this.startNext();
            }

            @Override
            protected void stop() {
                this.stopNext();
                Logs.CORE.info("开始停止Gateway服务");
                long begin = System.currentTimeMillis();
                gatewayServer.stop();
                Logs.CORE.info("停止Gateway服务完成, 耗时:%d ms", (System.currentTimeMillis() - begin));
            }
        }).end();
    }

    public void start() {
        if (!this.stop) {
            return;
        }
        this.stop = false;
        FileUtil.writeFile("starting", "");
        //接口Id初始化
        CommandId.init();
        //main初始化
        Main.I.init();
        Main.I.lunch.start();
        FileUtil.delete("starting");
    }

    public void stop() {
        if (this.stop) {
            return;
        }
        FileUtil.writeFile("stopping", "");
        Logs.CORE.info("服务器开始停止");
        Main.I.lunch.stop();
        Logs.CORE.info("服务器停止完成");
        FileUtil.delete("stopping");
        Main.I.stop = true;
    }

    private void checkOne() {
        TimerHolder.getTimer().newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                Logs.CORE.debug("开始1分钟检查");
                long begin = System.nanoTime();
                ClubActivityManager.I.checkExpire(System.currentTimeMillis());
                long cost = System.nanoTime() - begin;
                Logs.CORE.debug("开始1分钟检查结束, 耗时:%d", cost);
                checkOne();
            }
        }, 1, TimeUnit.MINUTES);
    }

    private void checkSave() {
        TimerHolder.getTimer().newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                Logs.CORE.debug("开始保存");
                long begin = System.nanoTime();
                int playerCnt = PlayerManager.I.save();
                int clubInfoCount = ClubManager.I.save();
                int roomCnt = RoomManager.I.save();
                int boxRoomScoreCnt = BoxManager.I.save();
                int mailBoxCnt = MailBoxManager.I.save();
                long cost = System.nanoTime() - begin;
                Logs.CORE.debug("开始保存结束, 保存玩家:%d 保存群组:%d 保存房间:%d 保存包厢战绩:%d 保存邮箱:%d 耗时:%d", playerCnt, clubInfoCount, roomCnt, boxRoomScoreCnt, mailBoxCnt, cost);
                redirectIOStreams(false);
                Logs.CMD.warn(GateStat.getInstance().report());
                checkSave();
            }
        }, 5, TimeUnit.MINUTES);
    }

    private void checkZero() {
        TimerHolder.getTimer().newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                Logs.CORE.debug("零点");
                long now = System.currentTimeMillis();
                ClubManager.I.zero(now);
                redirectIOStreams(true);
                saveAccountRemain();
                ClubActivityManager.I.zero(now);
                NewRankManager.I.zero(now);
                ClubHelperManager.I.zero();
                DownLineGameManager.I.zero(now);
                checkZero();
            }
        }, TimeUtil.ONE_DAY_MS - (System.currentTimeMillis() - TimeUtil.getZeroTimestampWithToday()), TimeUnit.MILLISECONDS);
    }

    private void saveAccountRemain() {
        Logs.CORE.debug("开始统计昨日留存");
        long begin = System.nanoTime();
        StatManager.I.calcYesterdayRemind();
        long cost = System.nanoTime() - begin;
        Logs.CORE.debug("统计昨日留存结束, 耗时:%d", cost);
    }

    private void checkSyncForStats() {
        TimerHolder.getTimer().newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                long begin = System.nanoTime();
                int count = StatManager.I.syncOnce();
                Logs.CORE.debug("同步统计数据数量:%d, 耗时:%d ms", count, System.nanoTime() - begin);
                checkSyncForStats();
            }
        }, 1, TimeUnit.MINUTES);
    }

    private void redirectIOStreams(boolean force) throws IOException {
        if (OSUtil.isLinux()) {
            File file = new File(Config.SYSTEM_OUT_PATH);
            if (!force && (!file.exists() || !file.isFile() || file.length() < NumberUtils._500M)) {
                file = null;
                return;
            }
            file = null;
            System.setOut(System.out);
            System.setErr(System.err);
            if (null != Main.this.fileOutputStream) {
                Main.this.fileOutputStream.flush();
                Main.this.fileOutputStream.close();
            }
            if (FileUtil.moveTo(Config.SYSTEM_OUT_PATH, String.format("allLogs/%s_%s", Config.SYSTEM_OUT_PATH, TimeUtil.format("yyyy_MM_dd_HH_mm_ss", System.currentTimeMillis())))) {
                System.out.println("success");
            } else {
                System.out.println("fail");
            }
            Main.this.fileOutputStream = new FileOutputStream(Config.SYSTEM_OUT_PATH, true);
            Main.this.ps = new PrintStream(Main.this.fileOutputStream, true);
            System.setOut(Main.this.ps);
            System.setErr(Main.this.ps);
        }
    }

    public AsyncMessageProcess getAsyncMessageProcess() {
        return asyncMessageProcess;
    }
    
    public ClubMessageProcess getClubMessageProcess() {
        return clubMessageProcess;
    }

    public AccountServer getAccountServer() {
        return accountServer;
    }

    public GatewayServer getGatewayServer() {
        return gatewayServer;
    }

    public RedissonClient getRedisson() {
        return redisson;
    }

    public LoginMessageProcess getLoginMessageProcess() {
        return loginMessageProcess;
    }

    public ChatMessageProcess getChatMessageProcess() {
        return chatMessageProcess;
    }

    public PlayerMessageProcess getPlayerMessageProcess() {
        return playerMessageProcess;
    }
    
    public BoxMessageProcess getBoxMessageProcess() {
        return boxMessageProcess;
    }

    public RoomMessageProcess getRoomMessageProcess() {
        return roomMessageProcess;
    }

    public BootChain getLunch() {
        return lunch;
    }
}
