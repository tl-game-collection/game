package com.xiuxiu.app.server.room.normal.mahjong;

public enum EHuType {
    // 卡五星开始
    HU(1, 1),                                   // 屁胡
    PPH(2, 2),                                  // 碰碰胡
    SZY(8, 4),                                  // 手抓一
    MSG(3, 2),                                  // 明四归
    MSG_S(74, 4),                               // 双明四归
    ASG(7, 4),                                  // 暗四归
    QD(6, 4),                                   // 七对
    LQD(11, 8),                                 // 龙七对
    SLQD(12, 16),                               // 双龙七对
    SANLQD(13, 32),                             // 三龙七对
    KWX(4, 2),                                  // 卡五星
    KWX_MSG(72, 4),                             // 卡五星明四归
    KWX_MSG_S(77, 16),                          // 卡五星双明四归
    KWX_ASG(73, 8),                             // 卡五星暗四归
    KWX_ASG_S(78, 16),                          // 卡五星双暗四归
    KWX_MSG_ASG(79, 16),                        // 卡五星明四归暗四归
    QYS(5, 4),                                  // 清一色
    ASG_S(75, 8),                               // 双暗四归
    MSG_ASG(76, 8),                             // 明四归暗四归
    XSY(9, 4),                                  // 小三元
    DSY(10, 8),                                 // 大三元
    QYS_PPH(51, 8),                             // 清一色碰碰胡
    QYS_QD(52, 8),                              // 清一色七对
    QYS_SZY(53, 8),                             // 清一色手抓一
    QYS_KWX(54, 8),                             // 清一色卡五星
    QYS_MSG(55, 8),                             // 清一色明四归
    QYS_MSG_S(80, 16),                          // 清一色双明四归
    QYS_ASG(56, 8),                             // 清一色暗四归
    QYS_ASG_S(81, 16),                          // 清一色双暗四归
    QYS_LQD(57, 16),                            // 清一色龙七对
    QYS_SLQD(58, 32),                           // 清一色双龙七对
    QYS_SANLQD(59, 64),                         // 清一色三龙七对
    QYS_KWX_MSG_S(83, 32),                      // 清一色卡五星双明四归
    QYS_KWX_ASG_S(84, 32),                      // 清一色卡五星双暗四归
    QYS_MSG_ASG(82, 16),                        // 清一色明四归暗四归
    QYS_KWX_MSG_ASG(85, 32),                    // 清一色卡五星明四归暗四归
    XSY_SZY(60, 8),                             // 小三元手抓一
    XSY_PPH(61, 8),                             // 小三阳碰碰胡
    XSY_MSG(62, 8),                             // 小三阳明四归
    XSY_ASG(63, 8),                             // 小三阳暗四归
    XSY_KWX(64, 8),                             // 小三阳卡五星
    XSY_QD(14, 8),                              // 小三阳七对
    DSY_PPH(65, 16),                            // 大三元碰碰胡
    DSY_SZY(66, 16),                            // 大三元手抓一
    DSY_KWX(67, 16),                            // 大三元卡五星
    QYS_MSG_KWX(68, 16),                        // 清一色明四归卡五星
    QYS_ASG_KWX(69, 32),                        // 清一色暗四归卡五星
    XSY_MSG_KWX(70, 16),                        // 小三阳明四归卡五星
    XSY_ASG_KWX(71, 32),                        // 小三阳暗四归卡五星
    GSKH(101, 0),                               // 杠上胡
    GSP(102, 0),                                // 杠上炮
    QGH(103, 0),                                // 枪杆胡
    LP(104, 0),                                 // 亮牌
    HDLY(105, 0),                               // 海底捞月
    HDP(106, 0),                                // 海底炮
    BAR_CNT(107, 0),                            // 连杠次数
    SK(108, 0),                                 // 数坎
    PQMB(109, 0),                               // 跑恰模八
    STATE_TING(111, 0),                         // 听牌状态
    STATE_LP(112, 0),                           // 亮牌状态
    SHANG_LOU(113, 0),                          // 上楼状态
    WILL(114, 0),                               // 将
    POINT(115, 0),                              // 点
    POINT_HU(116, 0),                           // 胡点
    POINT_ZFB(117, 0),                          // 中发白
    // 卡五星结束
    //荆楚晃晃
    JCHH_SOFT(1014, 1),                         // 软自摸
    JCHH_HARD(1015, 2),                         // 硬自摸
    JCHH_TIANHU(1016, 2),                       // 天胡
    // 一脚癞油开始
    YJLY_SOFT(1001, 2),                         // 软自摸
    YJLY_HARD(1002, 4),                         // 硬自摸
    YJLY_SOFT_LAI(1003, 2),                     // 软癞油
    YJLY_HARD_LAI(1004, 4),                     // 硬癞油
    YJLY_CHAO_TIAN(1005, 2),                    // 朝天
    YJLY_GSKH(1006, 2),                         // 杠上开花
    YJLY_LAI(1007, 0),                          // 癞子
    YJLY_YZY(1008, 0),                          // 油中油
    YJLY_DPH(1009, 2),                          // 点炮胡
    YJLY_QGH(1010, 3),                          // 抢杠胡
    YJLY_MGFP(1011, 5),                         // 明杠放炮
    YJLY_FGFP(1012, 3),                         // 放杠放炮
    YJLY_AGFP(1013, 8),                         // 暗杠放炮
    YJLY_XIQIAN(1017, 1),                       // 喜钱
    // 一脚癞油结束
    MAX(10000000, 0),                           // 最大
    ;
    private int value;
    private int fan;

    EHuType(int value, int fan) {
        this.value = value;
        this.fan = fan;
    }

    public int getValue() {
        return this.value;
    }

    public int getFan() {
        return fan;
    }
}
