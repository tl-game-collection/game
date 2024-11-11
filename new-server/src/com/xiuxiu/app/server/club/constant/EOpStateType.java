package com.xiuxiu.app.server.club.constant;

/**
 *
 */
public enum EOpStateType {
    NORMAL,             // 0、正常
    AGREE,              // 1、同意
    REJECT,             // 2、拒绝
    PROMOTION,          // 3、提升为副圈主
    DECLINE,            // 4、下降为成员
    FORBID,             // 5、禁玩
    NOT_FORBID,         // 6、取消禁玩
    DELETE,             // 7、删除
    LEAVE,              // 8、离开
    PROMOTION_ADMIN,    // 9、提升为管理员
    CANCEL_ADMIN,       // 10、取消管理员
    WAIT_OTHER_DEAL,    // 11、等待他人处理
    WAIT_TIME_OUT       // 12、超时未处理
    ;

}
