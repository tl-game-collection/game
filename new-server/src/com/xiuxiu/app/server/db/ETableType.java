package com.xiuxiu.app.server.db;

import java.util.concurrent.TimeUnit;

public enum ETableType {
    TB_ACCOUNT("xx_db_account_", true),
    TB_ACCOUNT_TOKEN("xx_db_account_token_", true),
    TB_ACCOUNT_PHONE("xx_db_account_phone_", true),
    TB_ACCOUNT_UID("xx_db_account_uid_"),
    TB_PLAYER("xx_db_player_", true),
    TB_LOCATION_INFO("xx_db_location_info_uid_", true, 30, TimeUnit.DAYS),
    TB_LOG_ACCOUNT("xx_db_log_account"),
    TB_LOG_ACCOUNT_REMAIN("xx_db_log_account_remain"),
    TB_ASSISTANT_WECHAT("xx_db_assistant_wechat_uid_"),
    TB_MAILBOX("xx_db_mail_box_"),
    TB_MAILBOX_UID("xx_db_mail_box_uid_"),
    TB_TODAY_STATISTICS("xx_db_today_statistics_"),
    TB_RANK_DATA("xx_db_rank_data_"),
    TB_MONEY_EXPEND_RECORD("xx_db_money_expend_record_"),
    TB_MONEY_EXPEND_RECORD_DETAILS("xx_db_money_expend_record_details"),
    TB_PLAYER_MONEY_CONSUME_RECORD("xx_db_player_money_consume_record_"),
    TB_FORBID("xx_db_forbid"),
    TB_FLOOR("xx_db_floor_"),
    TB_BOX("xx_db_box_"),
    TB_BOX_SCORE("xx_db_box_score_"),
    TB_BOX_SCORE_PLAYER("xx_db_room_score_player"),
    TB_ROOM_SCORE("xx_db_room_score_"),
    TB_ARENA_SCORE_DETAIL("xx_db_arena_score_detail_"),
    TB_CLUB_INFO("xx_db_clubInfo"),
    TB_CLUE_MEMBER("xx_db_clubmember"),
    TB_CLUB_MEMBER_EXT("xx_db_member_ext"),
    TB_CLUB_ACTIVITY("xx_db_clubActivity"),
    TB_CLUB_GOLD_RECORD("xx_db_club_gold_record_"),
    TB_CLUB_REWARD_VALUE_RECORD("xx_db_club_reward_value_record_"),
    TB_CLUB_ACTIVITY_GOLD_REWARD_RECORD("xx_db_club_activity_gold_reward_record_"),
    TB_CLUB_UID("xx_db_club_uid_"),
    TB_UNIQUE_CODE("xx_db_unique_code_"),
    TB_UPDOWN_GOLD_ORDER("xx_db_updown_gold_order_"),
    TB_DOWN_LINE_GAME_RECORD("xx_db_down_line_game_record_"),
    TB_BOX_ARENA_SCORE("xx_db_box_arena_score_"),
    TB_BOX_ARENA_SCORE_INFO("xx_db_box_arena_score_info_"),
    TB_BOX_ARENA_SCORE_INFO_PLAYER_ID("xx_db_box_arena_score_info_player_id"),
    TB_HUNDRED_REB_RECORD("xx_hundred_reb_record_"),
    TB_HUNDRED_BUREAU_RECORD("xx_hundred_bureau_record_"),
    
    /**********lcadd**********/
    TB_NOTICE("xx_db_notice_", true),
    ;

    private String redisKey;
    private boolean cache;
    private int expire = -1;
    private TimeUnit timeUnit = TimeUnit.HOURS;

    ETableType(String redisKey) {
        this(redisKey, false);
    }

    ETableType(String redisKey, boolean cache) {
        this(redisKey, cache, 1, TimeUnit.HOURS);
    }

    ETableType(String redisKey, boolean cache, int expire, TimeUnit timeUnit) {
        this.redisKey = redisKey;
        this.cache = cache;
        this.expire = expire;
        this.timeUnit = timeUnit;
    }

    public String getRedisKey() {
        return this.redisKey;
    }

    public boolean isCache() {
        return cache;
    }

    public int getExpire() {
        return expire;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
