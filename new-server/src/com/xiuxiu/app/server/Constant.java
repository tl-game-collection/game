package com.xiuxiu.app.server;

import java.util.regex.Pattern;

public final class Constant {
    // 线程数（Runtime.getRuntime().availableProcessors():获取cpu核心bai数）
    public static final int MAX_THREAD_CNT = Runtime.getRuntime().availableProcessors() * 2;

    // 最多申请好友数量
    public static final int MAX_APPLY_CNT = 25;
    public static final int MAX_OPERATION_CNT = 25;
    // 竞技场玩家最小距离
    public static final int ARENA_MIN_DISTANCEE = 0;
    // 房间解散等待时间
    public static final long ROOM_DISSOLVE_WAIT_TIME = 90 * 1000L;              // 房间解散等待时间
    public static final long ROOM_TAKE_TIMEOUT = 30 * 1000;                     // 房间打牌超时

    // 每页个数
    public static final int PAGE_CNT_10 = 10;
    public static final int PAGE_CNT_20 = 20;
    public static final int PAGE_CNT_30 = 30;
    public static final int PAGE_CNT_100 = 100;
    public static final int PAGE_CNT_MAX = 500;                                 //每页个数上限

    // 群组上限
    public static final int GROUP_MAX_MEMBER = 500;
    // 用户展示图片数量
    public static final int SHOW_IMAGE_CNT = 9;

    // 名字长度
    public static final int LEN_NAME = 250;
    // 个性签名长度
    public static final int LEN_SIGNATURE = 1000;
    // 位置长度
    public static final int LEN_ZONE = 250;
    // 图片长度
    public static final int LEN_IMAGE = 250;

    // 竞技场开启有效时间（半年）
    public static final int ARENA_TIME_LIMIT = 259200;//小时
    //财务上下分最高值
    public static final int GROUP_RECHARGE_LIMIT = 1000000;
    //玩家下分间隔时间
    public static final int DOWN_GOLD_TIME_SPACE = 30*60*1000;//毫秒

    // 群组竞技
    public static final int GROUP_ARENA_LIMIT = 10; // 群竞技上限

    //群组任务
    public static final int GROUP_QUEST_LIMIT = 5;// 群组任务上限
    
    // 平台财务账号起始UID
    public static final long PLATFORM_FINANCE_UID_BEGIN = 11001;
    // 11001~11005为上分
    public static final long PLATFORM_RECHARGE_FINANCE_END = 11005;
    // 11006~11010为下分
    public static final long PLATFORM_WITHDRAW_FINANCE_BEGIN = 11006;
    // 平台财务账号截止UID
    public static final long PLATFORM_FINANCE_UID_END = 11010;


    //群组自动升为副群主的分成比例
    public static final int GROUP_ARENA_DIVIDE = 0;             //竞技场分成比例，针对成员
    public static final int GROUP_ARENA_DIVIDE_LINE = 0;        //竞技场分成比例，针对
    public static final int GROUP_DEL_PLAYER_LIMIT = 5000;      // 群删除的人数列表
    
   //群组任务
   public static final int TEXAS_SURPASS_CARDS_LIMIT = 17;// 德州反超牌上限

    public static final int ARENA_VALUE_ODDS = 100;//竞技值发给客户端要除以100
    
    public static final int ODDS = 100;//100

    public static final int FRIENDS_CIRCLE_CONSUME = 200;   // 亲友圈消耗

    public static final int RECOMMEND_DIAMOND = 300;        // 分享有奖领取的上限

    public static final int INVITORNEEDCOUNT = 1;

    public static final int DIAMOND_CONVERSION = 5; // 玩家房卡兑换竞技分

    //CLUB 相关
    public static final int CLUB_MAX_APPLY_MERGE_CNT = 25; //最大申请合并请求列表数量
    public static final int CLUB_APPLY_MERGE_EXT_CNT = 5; //最大申请合并请求列表冗余数
    public static final int CLUB_APPLY_MERGE_INVALID_TIME = 3600000; //发起合并申请有效时间
    public static final int CLUB_APPLY_MERGE_LOCK_TIME = CLUB_APPLY_MERGE_INVALID_TIME + 30000; //发起合并申请的一方锁定部分操作时间(毫秒)

    public final static Pattern PATTERN_NAME = Pattern.compile("^[\\u4E00-\\u9FA5A-Za-z0-9 ]+$");
    public final static Pattern PATTERN_PHONE = Pattern.compile("^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$");

}
