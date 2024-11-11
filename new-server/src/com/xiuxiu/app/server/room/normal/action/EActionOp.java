package com.xiuxiu.app.server.room.normal.action;

public enum EActionOp {
    NORMAL,                             // 初始化
    PASS,                               // 跳过
    EAT,                                // 吃
    BUMP,                               // 碰
    BAR,                                // 杠
    MUST_BAR,                           // 必须杠
    HU,                                 // 胡
    FUMBLE,                             // 发牌
    TAKE,                               // 打牌
    HU_WIATH_BAR,                       // 杠上胡
    WAIT,                               // 等待
    WAIT_PASS,                          // 等待 跳过
    WAIT_SELECT,                        // 等待选择
    DISSOLVE_WAIT,                      // 解散等待
    DISSOLVE_REJECT,                    // 解散拒绝
    DISSOLVE_AGREE,                     // 解散同意
    READY,                              // 准备
    BRIGHT,                             // 亮牌
    FLUTTER,                            // 飘
    SHUKAN,                             // 数坎
    HUAN_PAI,                           // 换牌
    SHUAI_PAI,                          // 甩牌
    DING_QUE,                           // 定缺
    XUAN_ZENG,                          // 选增
    XUAN_PIAO,                          // 选票
    BAR_SCORE,                          // 杠分
    RESULT,                             // 结果
    BOMB_SCORE,                         // 炸弹分数
    ROB_BANKER,                         // 抢庄
    CALL_SCORE,                         // 叫分
    BANKER,                             // 庄
    REBET,                              // 下注
    DEAL_CARD,                          // 发牌
    SHOW_LAST_CARD,                     // 显示底牌
    SHOW_ALL_CARD,                      // 明牌
    AUTO_START,                         // 自动开始
    DELAY,                              // 延迟执行
    LOOK_CARD,                          // 看牌
    DISCARD,                            // 弃牌
    ADD_NOTE,                           // 加注
    FOLLOW_NOTE,                        // 跟注
    COMPARE,                            // 比牌
    CHECK,                              // 过牌
    ALLIN,                              // all in
    DEAL_RED_ENVELOPE,                  // 发红包
    TEXAS_BASALIS,                      // 德州分池动作
    SHOW_LAIZI_CARD,                    // 癞子牌
    SHOW_OFF,                           // 炫耀
    MO_CARD,                            // 摸牌
    OPEN_CARD,                          // 开牌
    TUI_GOLD,                           // 推金
    DESK_SHOW,                          // 牌桌显示标识
    START_HU,                           // 起手胡
    CS_START_HU,                        // 长沙起手胡
    CS_MIDDLE_HU,                       // 长沙中途胡
    OPEN_BAR,                           // 开杠
    LAST_CARD,                          // 最后一张操作
    YANG_PAI,                           // 仰牌
    TING,                               // 报听
    HOT_AGAIN,                          // 续锅
    HOT_OUT,                            // 揭锅
    SEND_CARD,                          // 发牌
    PRIMULA,                            // 叫春
    TIMEOUT_DISSOLVE                    // 超时解散房间
    ;

    @Override
    public String toString() {
        return this.name();
    }
}
