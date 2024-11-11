package com.xiuxiu.app.server.room.normal.poker.cow;

/**
 * @auther: yuyunfei
 * @date: 2020/1/6 17:30
 * @comment:
 */
public enum ECowPlayTypes {
    // 1:自由抢庄, 2:花式抢庄, 3:霸王庄(竞技场才有), 4:明牌抢庄, 5:通比玩法, 6:端火锅
    NORMAL,                 // 无
    ZY_ROB_BANKER,          // 自由抢庄
    HS_ROB_BANKER,          // 花式抢庄
    OVERLORD_BANKER,        // 霸王庄
    MP_ROB_BANKER,          // 明牌抢庄
    COMMON_PLAYING,         // 通比玩法
    HOT_POT,                // 端火锅
    ;
}
