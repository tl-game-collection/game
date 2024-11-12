package com.xiuxiu.app.server;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.protocol.api.temp.player.PCLIWhiteListInfo;
import com.xiuxiu.core.utils.HttpUtil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;
import com.xiuxiu.core.utils.TimeUtil;
import com.xiuxiu.sms.SmsConfig;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
    // server info
    public static String LOGIN_SERVER_HOST = "0.0.0.0";
    public static int LOGIN_SERVER_PORT = 2300;
    public static String API_SERVER_HOST = "0.0.0.0";
    public static int API_SERVER_PORT = 2301;
    public static String GATEWAY_SERVER_IP = "0.0.0.0";
    public static String GATEWAY_SERVER_HOST = "54.254.199.232";
    public static int GATEWAY_SERVER_PORT = 2000;
    public static int PLAYER_PROCESS_THREAD_NUM = Constant.MAX_THREAD_CNT;
    public static int ROOM_PROCESS_THREAD_NUM = Constant.MAX_THREAD_CNT;
    public static int ARENA_PROCESS_THREAD_NUM = Constant.MAX_THREAD_CNT;
    public static int HUNDRED_ARENA_PROCESS_THREAD_NUM = Constant.MAX_THREAD_CNT;
    public static int PLAY_FIELD_PROCESS_THREAD_NUM = Constant.MAX_THREAD_CNT;
    public static int BOX_PROCESS_THREAD_NUM = Constant.MAX_THREAD_CNT;
    public static boolean DEVELOP = true;

    public static String FILE_UPLOAD_SERVER_URL = "";
    public static String FILE_DOWNLOAD_SERVER_URL = "";

    // public static String AGENCY_URL = "http://api.agency.7pr8.cn:1235/";
    public static String AGENCY_URL = "http://192.168.2.45:8080/";

    // room record save path
    public static String RECORD_PATH = "record";

    // db info
    public static String MYSQL_CONFIG_PATH = "cnf/mysql.xml";

    // redis info
//    public static String REDIS_URL = "redis://r-bp1u6tt8pm66griq4s.redis.rds.aliyuncs.com:6379";
    public static String REDIS_URL = "redis://redis:6379";
    // login info
    public static String APP_KEY = "#u)%P/Wht$~SQlzcq";

    // wechat info
    public static String[] WECHAT_APP_ID = new String[] {};
    public static String[] WECHAT_APP_SECRET = new String[] {};

    // jpush info
    public static String JPUSH_APP_KEY = "";
    public static String JPUSH_MASTER_SECRET = "";

    // dingdingapicongfig info
    public static String DINGDING_APP_KEY = "dingoa0qadfkf1ionvboc1";
    public static String DINGDING_APP_SECRET = "HPsu1eckYGbXyhrQ-KzlX1FLY06oMf3R8gZqH1nihfna6CCXoRTOVOK--Cjaw-fm";

    // dingding robot
    public static String DINGDING_ROBOT_URL = "https://oapi.dingtalk.com/robot/send?access_token=6dabc004c8f16e63b34d25d1a06806c90f67fa414caef8d770ecb3aa2ad08e81";

    // sms info
    public static SmsConfig SMS_CONFIG = new SmsConfig();

    // 高德Map info
    public static String AMAP_KEY = "e290641cc97a7fbdfd9ef13c59e609e4";
    public static String AMAP_REGEO_URL = "https://restapi.amap.com/v3/geocode/regeo";

    // key-filter file path
    public static String KEY_FILTER_PATH = "cnf/key-filter.txt";

    // system out file path
    public static String SYSTEM_OUT_PATH = "allLog";

    // transfer ip by server
    public static HashMap<String, Integer> TRANSFER = new HashMap<>();

    // ip white
    public static HashSet<String> IP_WHITE = new HashSet<>();

    private static ConcurrentHashMap<Long, PCLIWhiteListInfo> WHITE = new ConcurrentHashMap<>();
    private static HashSet<Long> NORANK = new HashSet<>();

    // search
    public static long SEARCH_LIMIT = 10;                       // 搜索限制
    public static long SEARCH_ERR_LIMIT = 30;                   // 搜索错误限制
    public static long SEARCH_FORBID = TimeUtil.ONE_DAY_MS;     // 搜索禁止时间

    // biz channel to group uid
    public static ConcurrentHashMap<Integer, Long> bizChannel2GroupUid = new ConcurrentHashMap<>();

    public static String STATISTICS_URL = "http://statistics.com/api/";   // 统计服务接口URL

    public static String STATISTICS_API_KEY = "wVy0as$zUsWG$%G9";   //统计后台API KEY

    public static int WITHDRAW_FEE = 2;                                   // 提现费率 百分比

    static {
        //TRANSFER.put("192.168.1.46", 1000);

        //IP_WHITE.add("172.16.24.54");
        //IP_WHITE.add("172.16.24.55");
        //IP_WHITE.add("47.98.217.75");
        IP_WHITE.add("127.0.0.1");
        IP_WHITE.add("192.168.1.11");
        IP_WHITE.add("192.168.1.220");
        IP_WHITE.add("192.168.1.172");

        bizChannel2GroupUid.putIfAbsent(1, 88888L);
        bizChannel2GroupUid.putIfAbsent(2, 88888L);
        bizChannel2GroupUid.putIfAbsent(3, 88888L);
        bizChannel2GroupUid.putIfAbsent(4, 88888L);
        bizChannel2GroupUid.putIfAbsent(5, 88888L);
        bizChannel2GroupUid.putIfAbsent(6, 88888L);
        bizChannel2GroupUid.putIfAbsent(7, 88888L);
        bizChannel2GroupUid.putIfAbsent(8, 88888L);
    }

    public static boolean checkWhiteHas(long playerUid, int type) {
        if (WHITE.containsKey(playerUid)) {
            //麻将扑克
            if (type == 1) {
                return WHITE.get(playerUid).PokerAndMajong;
            }
            //Niuniu
            else if (type == 2) {
                return WHITE.get(playerUid).NiuNiu;
            }
            //Jinhua
            else if (type == 3) {
                return WHITE.get(playerUid).JinHua;
            }
          //SanGong
            else if (type == 4) {
                return WHITE.get(playerUid).SanGong;
            }
        }
        return false;
    }

    public static ConcurrentHashMap<Long, PCLIWhiteListInfo> getWHITE() {
        ConcurrentHashMap<Long,PCLIWhiteListInfo> tempSet = new ConcurrentHashMap<>();
        tempSet.putAll(WHITE);
        //Logs.API.warn("获取WHITE 时间:%s WHITE:%s", TimeUtil.format("yyyy-MM-dd HH:mm:ss.SSS", System.currentTimeMillis()),WHITE);
        return tempSet;
    }

    public static void addWHITE(long playerUid, boolean majong, boolean niuniu, boolean jinhua, boolean sangong) {
        PCLIWhiteListInfo info = new PCLIWhiteListInfo();
        info.PokerAndMajong = majong;
        info.NiuNiu = niuniu;
        info.JinHua = jinhua;
        info.SanGong = sangong;
        WHITE.put(playerUid, info);
    }

    public static void delWHITE(long playerUid) {
        if (WHITE.containsKey(playerUid)) {
            WHITE.remove(playerUid);
        }
    }

    public static void setWHITE(ConcurrentHashMap<Long, PCLIWhiteListInfo> WHITE) {
        Logs.API.warn("设置WHITE 时间:%s WHITE:%s", TimeUtil.format("yyyy-MM-dd HH:mm:ss.SSS", System.currentTimeMillis()),WHITE);
        Config.WHITE = WHITE;
    }

    private static void initWHITE() {
        Map<String, Object> param = new HashMap<>();
        param.put("sign", MD5Util.getMD5(Config.STATISTICS_API_KEY));
        String jsonStr = JsonUtil.toJson(param);
        try {
            String result = Config.post(Config.STATISTICS_URL + "getVipList", jsonStr);
            if (result == null) {
                return;
            }
            System.out.println(result);
            ConcurrentHashMap<Long, PCLIWhiteListInfo> playerSet = JsonUtil.fromJson(result, new TypeReference<ConcurrentHashMap<Long, PCLIWhiteListInfo>>(){});
            if (playerSet == null) {
                playerSet = new ConcurrentHashMap<>();
            }
            Config.setWHITE(playerSet);
        }catch (Exception e){

        }
    }

    private static String post(String url, String jsonString){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-Type", "application/json");

        CloseableHttpResponse response = null;
        try {
            post.setEntity(new ByteArrayEntity(jsonString.getBytes("UTF-8")));
            response = httpClient.execute(post);
            if (null != response) {
                HttpEntity entity = response.getEntity();
                //result = EntityUtils.toString(entity);
                if(response.getStatusLine().getStatusCode() == 200){
                    Map<String, Object> maps = JsonUtil.fromJson(EntityUtils.toString(entity), HashMap.class);
                    if((Integer)maps.get("ret") == 200){
                        return maps.get("data").toString();
                    }
                }
            }

            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                if(response != null){
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static boolean checkNoRankHas(long playerUid) {
        return NORANK.contains(playerUid);
    }

    public static HashSet<Long> getNORANK() {
        HashSet<Long> tempSet = new HashSet<>();
        tempSet.addAll(NORANK);
        //Logs.API.warn("获取NORANK 时间:%s NORANK:%s", TimeUtil.format("yyyy-MM-dd HH:mm:ss.SSS", System.currentTimeMillis()),NORANK);
        return tempSet;
    }

    public static void addNORANK(long playerUid) {
        NORANK.add(playerUid);
    }

    public static void delNORANK(long playerUid) {
        NORANK.remove(playerUid);
    }

    public static void setNORANK(HashSet<Long> NORANK) {
        Logs.API.warn("设置NORANK 时间:%s NORANK:%s", TimeUtil.format("yyyy-MM-dd HH:mm:ss.SSS", System.currentTimeMillis()),NORANK);
        Config.NORANK = NORANK;
    }

    private static void initNORANK() {
        Map<String, Object> param = new HashMap<>();
        param.put("sign", MD5Util.getMD5(Config.STATISTICS_API_KEY));
        String jsonStr = JsonUtil.toJson(param);
        try {
            String result = Config.post(Config.STATISTICS_URL + "getNoRankList", jsonStr);
            if (result == null) {
                return;
            }
            System.out.println(result);
            HashSet<Long> playerSet = JsonUtil.fromJson(result, new TypeReference<HashSet<Long>>(){});
            if (playerSet == null) {
                playerSet = new HashSet<>();
            }
            Config.setNORANK(playerSet);
        }catch (Exception e){

        }
    }

    public static void initVipList() {
        Config.initWHITE();
        Config.initNORANK();
    }
}