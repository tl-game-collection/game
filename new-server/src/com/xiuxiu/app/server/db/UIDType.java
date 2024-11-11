package com.xiuxiu.app.server.db;

public enum UIDType {
    ACCOUNT("xx_uid_account", 20001L),
    ROOM("xx_uid_room"),
    CHAT("xx_uid_chat"),
    SCORE_ROOM("xx_uid_score_room"),
    SCORE_BOX("xx_uid_score_box"),
    SCORE_BOX_PLAYER("xx_uid_score_box_player"),
    MAIL("xx_uid_mail"),
    BOX("xx_uid_box"),
    FLOOR("xx_uid_floor"),
    RECORD("xx_uid_record"),
    RECOMMEND("xx_uid_recommend"),
    MAILBOX("xx_uid_mail_box"),
    LOG_ACCOUNT("xx_uid_log_account"),
    LOG_ACCOUNT_REMAIN("xx_uid_log_account_remain"),
    ASSISTANT_WECHAT("xx_uid_assistant_wechat"),
    LOCATION_INFO("xx_uid_location_info"),
    TODAY_STATISTICS("xx_uid_today_statistics"),
    RANK_DATA("xx_uid_rank_data"),
    MONEY_EXPEND_RECORD("xx_uid_money_expend_record"),
    MONEY_EXPEND_RECORD_DETAIL("xx_uid_money_expend_record_detail"),
    PLAYER_MONEY_CONSUME_RECORD("xx_uid_player_money_consume_record"),
    FORBID("xx_uid_forbid"),
    CLUB_MEMBER("xx_uid_clubmember"),
    CLUB_ACTIVITY("xx_uid_activity"),
    CLUB_MEMBER_EXT("xx_uid_club_member_ext"),
    CLUB_GOLD_RECORD("xx_uid_club_gold_record"),
    CLUB_REWARD_VALUE_RECORD("xx_uid_club_reward_value_record"),
    CLUB_ACTIVITY_GOLD_REWARD_RECORD("xx_uid_clubActivityGoldRewardRecord"),
    UPDOWN_GOLD_ORDER("xx_uid_updown_gold_order"),
    BOX_ARENA_SCORE("xx_uid_box_arena_score"),
    BOX_ARENA_SCORE_INFO("xx_uid_box_arena_score_info"),
    BOX_ARENA_SCORE_INFO_PLAYER_ID("xx_uid_box_arena_score_info_player_id"),
    HUNDRED_REB_RECORD("xx_uid_hundred_reb_record"),
    HUNDRED_BUREAU_RECORD("xx_uid_hundred_bureau_record"),
            ;

    private String key;
    private long initValue;

    UIDType(String key) {
        this(key, 1);
    }

    UIDType(String key, long initValue) {
        this.key = key;
        this.initValue = initValue;
    }

    public String getKey() {
        return key;
    }

    public long getInitValue() {
        return initValue;
    }

    @Override
    public String toString() {
        return "UIDType{" +
                "key='" + key + '\'' +
                ", initValue=" + initValue +
                '}';
    }
}
