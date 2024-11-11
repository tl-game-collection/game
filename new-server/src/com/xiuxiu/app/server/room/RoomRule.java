package com.xiuxiu.app.server.room;

public class RoomRule {
    public static final String RR_NONE = "none";                                                                        // 无
    // 通用
    public static final String RR_BUREAU = "bureau";                                                                    // 局数
    public static final String RR_SMALL_BUREAU = "smallBureau";                                                         // 小局数
    public static final String RR_PLAYER_NUM = "playerNum";                                                             // 人数
    public static final String RR_PLAYER_MIN_NUM = "playerMinNum";                                                      // 最低人数要求
    public static final String RR_END_POINT = "endPoint";                                                               // 底分
    public static final String RR_TOP = "top";                                                                          // 封顶, -1: 不封顶
    public static final String RR_PLAY = "playMethod";                                                                  // 玩法
    public static final String RR_CUR_LOOP = "curLoop";                                                                 // 当前轮数
    public static final String RR_TOTAL_LOOP = "totalLoop";                                                             // 总轮数
    public static final String RR_DEPUTY_DIVIDE = "deputyDivide";                                                       // 限制竞技值
    public static final String RR_MIN_BUREAU = "minBureau";                                                             // 最低局数要求
    public static final String RR_MAX_BUREAU = "maxBureau";                                                             // 最高局数要求
    public static final String RR_GAIN_VALUE = "gainValue";                                                             // 打赏金额
    public static final String RR_WIN_GAIN_VALUE = "winGainValue";                                                      // 大赢家打赏金额
    public static final String RR_MINGOLD = "minArena";                                                                  // 入场分数
    public static final String RR_LEAVEGOLD = "leaveArenaValue";                                                          // 出场分数
    public static final String RR_TIYAN = "tiyan";                                                                      // 是否是体验场(1普通2体验)

    // 麻将
    public static final String RR_MJ_MAX_FANG = "maxFang";                                                              // 最大番数
    public static final String RR_MJ_FLUTTER = "flutter";                                                               // 选飘模式(1不漂,2每局选漂,3玩家定漂,4固定定漂)
    public static final String RR_MJ_FLUTTER_VALUE = "flutterValue";                                                    // 固定定漂值(麻将 1/2 跑得快1/2/3)
    public static final String RR_MJ_BUY_HORSE = "buyHorse";                                                            // 买马模式
    public static final String RR_MJ_SHU_KAN = "shuKan";                                                                // 数坎
    public static final String RR_MJ_HUAN_PAI = "huanPai";                                                              // 换牌
    public static final String RR_MJ_SHUAI_PAI = "shuaiPai";                                                            // 甩牌
    public static final String RR_MJ_TOP = "mjTop";                                                                     // 麻将
    public static final String RR_MJ_TIMEOUT = "timeout";                                                               // 操作时间
    // 一脚癞油
    public static final String RR_MJ_LAI_YOU = "laiYou";                                                                // 赖油 0: 一脚赖油, 1: 半癞, 2: 无癞到底
    // 汉川晃晃
    public static final String RR_MJ_LAIZI_TYPE = "laiZiType";                                                          // 癞子类型, 1: 一癞到底, 2: 多赖
    public static final String RR_MJ_XI_QIAN = "xiQian";                                                                // 喜钱: 0: 无, 5, 10
    // 宜昌血流
    public static final String RR_MJ_HUAN_PAI_TYPE = "buanPaiType";                                                     // 换牌类型, 0: 换三张, 1: 甩三张, 2: 先换在甩
    public static final String RR_MJ_DING_PIAO = "dingPiao";                                                            // 定漂, 0, 1, 2, 3, 4, 5, 6
    // 武汉麻将
    public static final String RR_MJ_WH_BAR_TYPE = "barType";                                                           // 杠类型, 1: 中发杠, 2: 中发白杠, 3: 中发皮杠
    public static final String RR_MJ_WH_BAG_TYPE = "bagType";                                                           // 包类型: 0: 反包, 1: 陪包
    public static final String RR_MJ_WH_BEGIN_HU_FANG = "beginHuFang";                                                  // 起胡番数
    public static final String RR_MJ_WH_BEGIN_HU_POINT = "minPoints";                                                   // 起胡点数

    // 赤壁剁刀
    public static final String RR_MJ_CBDD_ZENG = "zengType";                                                            // 选增, 0: 不增, 4: 每句选真, 1: : 增1, 2: 增2, 3: 增3
    public static final String RR_MJ_CBDD_NIAO = "niaoType";                                                            // 抓鸟: 1: 147, 2: 258, 3: 369, 0: 无
    public static final String RR_MJ_CBDD_NIAO_NUM = "niaoNum";                                                         // 抓鸟张数: 1, 2, 3, 4, 5, 6
    // 红中麻将
    public static final String RR_MJ_HZ_BANKER_TYPE = "bankerType";                                                     // 做庄类型, 0: 随机, 1: 先进房坐庄    2 轮庄
    public static final String RR_MJ_HZ_ZHONG_NUM = "zhongNum";                                                         // 红中数量, 4, 8
    public static final String RR_MJ_HZ_NIAO_SCORE = "niaoScore";                                                        // 抓鸟分数, 1: 1鸟1分, 2: 1鸟2分
    public static final String RR_MJ_HZ_NIAO_TYPE = "niaoType";                                                         // 抓鸟分数类型, 1: 胡牌方位, 2: 庄家方位, 3: 159中鸟, 4: 对位中鸟, 5: 摸几奖几, 0: 不抓鸟
    public static final String RR_MJ_HZ_HU_TYPE = "huType";                                                             // 胡类型, 2: 抓2鸟, 4: 抓4鸟, 6: 抓6鸟, 0: 不抓鸟

    // 扣点麻将
    public static final String RR_MJ_KD_PLAY_TYPE = "playType";                                                         // 0,常规玩法、1，风耗子、2，不带风、3，随机耗子
    public static final String RR_MJ_KD_JIA_FAN = "jiaFan";                                                             // 0,清一色、2，一条龙、3，七对、4，豪华七对

    // 血战到底
    public static final String RR_MJ_XZDD_ZIMO_TYPE = "ziMoType";                                                       // 自摸类型, 0: 自摸到底, 1: 自摸加番
    // 转转麻将
    public static final String RR_MJ_ZZ_BANKER_TYPE = "bankerType";                                                     // 做庄类型, 0: 随机, 1: 先进房坐庄
    public static final String RR_MJ_ZZ_NIAO_SCORE = "niaoScore";                                                        // 抓鸟分数, 1: 1鸟1分, 2: 1鸟2分
    public static final String RR_MJ_ZZ_NIAO_TYPE = "niaoType";                                                         // 抓鸟分数类型, 1: 胡牌方位, 2: 庄家方位, 3: 159中鸟, 4: 对位中鸟, 5: 摸几奖几, 0: 不抓鸟
    public static final String RR_MJ_ZZ_HU_TYPE = "huType";                                                             // 胡类型, 2: 抓2鸟, 4: 抓4鸟, 6: 抓6鸟, 0: 不抓鸟
    // 长沙麻将
    public static final String RR_MJ_CS_NIAO_SCORE = "niaoScore";                                                       // 抓鸟分数, 0: 1鸟1倍, 1: 1鸟1分, 2: 1鸟2分
    public static final String RR_MJ_CS_NIAO_TYPE = "niaoType";                                                         // 抓鸟分数类型, 1: 胡牌方位, 2: 庄家方位, 3: 159中鸟, 4: 对位中鸟, 5: 摸几奖几, 0: 不抓鸟
    public static final String RR_MJ_CS_HU_TYPE = "huType";                                                             // 胡类型, 2: 抓2鸟, 4: 抓4鸟, 6: 抓6鸟, 0: 不抓鸟
    public static final String RR_MJ_CS_START_HU_TYPE = "startHuType";                                                  // 起手胡类型; 2: 抓2鸟, 4: 抓4鸟, 6: 抓6鸟, 0: 不抓鸟
    public static final String RR_MJ_CS_ZENG = "zengType";                                                              // 选增, 0: 不增, 4: 每句选真, 1: : 增1, 2: 增2, 3: 增3

    // 斗地主
    public static final String RR_LANDLORD_PLAY = "landlordPlay";                                                       // 种类
    public static final String RR_LANDLORD_BOMB_TOP = "landlordBombTop";                                                // 炸弹封顶

    // 跑得快
    public static final String RR_RF_FRAME = "frame";                                                                   // 张数
    public static final String RR_RF_MUST_PUT = "mustPut";                                                              // 必出
    public static final String RR_RF_FORCING_MOVE = "forcingMove";                                                      // 先出 低位: 先出类型(1: 赢家先出, 2: 黑桃三先出), 高位: 类型选项(1: 黑桃三必出)
    public static final String RR_RF_THREE_TAKE = "threeTake";                                                          // 牌够只能三带2;可以三带X(0,1,2)
    public static final String RR_RF_OUT_TIME = "outTime";                                                              // 出牌时间

    // 牛牛
    public static final String RR_COW_BANKER_TYPE = "cowBankerType";                                                    // 庄类型 1: 自由抢庄, 2: 花式抢庄, 3: 霸王庄 4:明牌抢庄 5通比玩法
    public static final String RR_COW_KING_RAZZ_TYPE = "cowKingRazzType";                                               // 王癞子类型 1: 无, 2: 经典, 3: 疯狂 ,4: 任意赖子
    public static final String RR_COW_MULTIPLE = "cowMultiple";                                                         // 翻倍规则 1: 牛8,9 2倍, 牛牛 3倍, 2: 牛7,8,9 2倍, 牛牛 3倍 3: 牛7,8 2倍, 牛9 3倍 牛牛 4倍 4: 牛1-牛牛 1-10倍
    public static final String RR_COW_ROB_BANKER_MULTIPLE = "robBankerMultiple";                                        // 抢庄倍数 1: 1倍, 2: 2倍, 3: 3倍 4: 4倍
    public static final String RR_COW_END_POINT = "cowEndPoint";                                                        // 底分 1: 1/2 2: 2/4 3: 3/6 4: 4/8
    public static final String RR_COW_PUSH_NOTE_LIMIT = "pushNoteLimit";                                                // 推注限制 0: 无, 5: 5倍 10: 10倍 20: 20倍
    public static final String RR_COW_PUSH_BANKER_TYPE = "pushBankerType";                                              // 上庄类型 1: 房主 2: 轮流 3: 牛牛 4: 牌大 5:连庄
    public static final String RR_COW_BANKER_END_POINT = "bankerEndPoint";                                              // 庄家底分 数值 400 800
    public static final String RR_COW_PORKER_CARD_NUMBER = "cardNumber";                                                // 牛牛    0, 4, 3
    public static final String RR_COW_PORKER_CARD_HOT_LOOP = "cowHotLoop";                                              // 端火锅 最少几轮
    public static final String RR_COW_PORKER_CARD_HOT_NOTE = "cowHotNote";                                              // 端火锅 锅底
    public static final String RR_COW_PUSH_NOTE_TYPE = "cowPushNoteType";                                               // 推注类型；3.无，2闲家推注，1 抢庄推注 4 闲家推注 经典
    public static final String RR_COW_DEDUCT_TYPE = "cowDeductType";                                                    // 扣牌类型, 0: 全扣, 1: 扣4张, 2: 扣3张, 3: 扣2张
    public static final String RR_COW_END_POINT_MUL = "cowEndPointMul";                                                 // 牛牛底分倍数
    public static final String RR_COW_FIRST_REBET_PRE = "cowFirstRebetPre";                                             // 首局底注百分比
    public static final String RR_COW_NEXT_REBET_PRE = "cowNextRebetPre";                                               // 首局后底注百分比

    public static final String RR_COW_OP_TYPE = "opType";                                                               // 操作流程, 1: 先发牌后下注, 2: 先下注后发牌
    public static final String RR_COW_HOT_CNT = "hotCnt";                                                               // 连庄次数
    public static final String RR_COW_HOT_UP = "hotUp";                                                                // 连庄锅底倍数
    public static final String RR_COW_LEVEL_LESS_NOTE = "hotLevelLessNote";                                                     // 手动下庄最低分
    public static final String RR_COW_HOT_MAX_LOOP = "hotMaxLoop";                                                           // 一庄最大局数

    // 扎金花
    //  public static final String RR_FGF_SINGLE_NOTE_LIMIT = "singleNoteLimit";                                            // 单注上限: 10 50 100
    public static final String RR_FGF_MUST_LOOP = "mustLoop";                                                           // 必跟几轮: 0 1 2
    public static final String RR_FGF_LOOP_LIMIT = "loopLimit";                                                         // 轮数上限制: 20 15 10
    public static final String RR_FGF_BANKER_TYPE = "fgfBankerType";                                                    //0 赢家庄  1 轮流庄；
    public static final String RR_FGF_TIME_OUT = "fgfTimeout";                                                          //30s , 60s, 120s, 0不弃牌
    public static final String RR_FGF_THREE_AWARD = "fgfThreeAward";                                                    //豹子奖励 0 无  10，20；
    public static final String RR_FGF_COMPARE_RULE = "fgfCompareRule";                                                  //比牌规则 0 -比大小， 1-比花色，2-全比；；
    public static final String RR_FGF_SELECT_NOTE = "fgfSelectNote";                                                    //下注选择 0,1；
    public static final String RR_FGF_STUFFY_LOOP = "fgfStuffyLoop";                                                    //闷牌轮数 0（不限制） 其他次数就按1,2,3来定义
    public static final String RR_FGF_COMPARE_LOOP = "fgfCompareLoop";                                                  //比牌轮数  其他次数就按1,2,3来定义
    public static final String RR_FGF_END_POINT_MUL = "fgfEndPointMul";                                                 // 扎金花底分倍数

    // 十三水
    public static final String RR_THIRTEEN_ROOM_TYPE = "roomType";                                                      // 房间：普通房间，通杀房间 0, 1
    public static final String RR_THIRTEEN_HORSE_CARD = "horseCard";                                                    // 马牌：无，黑桃五，黑桃十，黑桃K
    public static final String RR_THIRTEEN_SHOOT = "shoot";                                                             // 打枪：计分加1，计分乘以2
    public static final String RR_THIRTEEN_TIME = "time";                                                               // 理牌：60s 90s 120s

    // 大菠萝
    public static final String RR_DIABLO_COLOR = "color";                                                               //颜色；
    public static final String RR_DIABLO_JUSTCOLOR = "justColor";                                                       //正色；
    public static final String RR_DIABLO_VICECOLOR = "viceColor";                                                       //副色；
    public static final String RR_DIABLO_GHOST = "ghostCard";                                                           //鬼牌
    public static final String RR_DIABLO_RUSH = "rush";                                                                 //冲牌
    public static final String RR_DIABLO_RATE = "rate";                                                                 //倍率
    public static final String RR_DIABLO_ENDPOINT = "endPoint";                                                         //底分

    //五十K
    public static final String RR_WSK_PORKER_NUM = "wskPokerNum";                                                       //几幅扑克 1：一副，2：二副
    //public static final String RR_WSK_FORCING_MOVE = "wskForcingMove";                                                // 先出 低位: 先出类型(1: 赢家先出, 2: 黑桃三先出), 高位: 类型选项(1: 黑桃三必出)
    public static final String RR_WSK_HASKING = "wskHasKing";                                                           //是否含有大小王 ，0 没有，1有；
    public static final String RR_WSK_BOOM_SCORE = "wskBoomScore";                                                      // 炸弹是否算分（1 算分、0 不算分）

    //德州
    public static final String RR_TEXAS_POKER_TIME = "timer";                                                           // 倒计时
    public static final String RR_TEXAS_POKER_SBLIND = "texassBlind";                                                   //小盲
    public static final String RR_TEXAS_POKER_BBLIND = "texasbBlind";                                                   //大盲
    public static final String RR_TEXAS_POKER_MINNUM = "texasMinNum";                                                   //最小携带
    public static final String RR_TEXAS_POKER_MAXNUM = "texasMaxNum";                                                   //最大携带
    public static final String RR_TEXAS_POKER_ANTE = "texasAnte";                                                       //前注                                    				// 初始竞技值/分数
    public static final String RR_TEXAS_INITIAL_POINTS = "initialPoints";                                               //带入初始分
    public static final String RR_TEXAS_INSURANCE_ODDS = "ruleInsuranceOdds";                                           //保险收益率
    public static final String RR_TEXAS_IS_OPEN_INSURANCE = "ruleIsOpenInsurance";                                      //是否开启保险功能
    

    // 百人场公共
    public static final String RR_HUNDRED_SEAT = "seat";                                                                // 座位数
    public static final String RR_HUNDRED_PLAYER_CNT = "playerCnt";                                                     // 闲家数
    public static final String RR_HUNDRED_UP_BANKER_VALUE = "upBankerValue";                                            // 上庄值
    public static final String RR_HUNDRED_DOWN_BANKER_VALUE = "downBankerValue";                                        // 下庄值
    public static final String RR_HUNDRED_PLAY_RULE = "playRule";                                                       // 玩法规则
    public static final String RR_HUNDRED_REB_TIME = "rebTime";                                                         // 下注时间
    public static final String RR_HUNDRED_OPEN_CARD_TEIM = "openCardTime";                                              // 开牌时间
    public static final String RR_HUNDRED_READY_TIME = "readyTime";                                                     // 准备时间
    public static final String RR_HUNDRED_OVER_TIME = "overTime";                                                       // 结束时间
    public static final String RR_HUNDRED_ROOM_TYPE = "cowMultiple";                                                    // 翻倍规则
    public static final String RR_HUNDRED_VIP_SEAT_LIMIT = "vipSeatLimit";                                              // vip座位限制
    public static final String RR_HUNDRED_VIP_SEAT_FANLI = "vipSeatFanli";                                              // vip座位返利
    public static final String RR_HUNDRED_VIP_SEAT_AUTOUP = "vipSeatAutoUp";                                            // vip座位自动站起
    public static final String RR_HUNDRED_REB_LIMIT = "rebLimit";                                                       // 下注限制

    //龙虎斗
    public static final String RR_HUNDRED_SPECIALTYPE = "specialType";                                                  // 特殊牌型
    public static final String RR_HUNDRED_MAXBUREAU = "maxBureau";                                                      // 坐庄局数
    public static final String RR_HUNDRED_UPBANKERORDER = "upBankerOrder";                                              // 上庄顺序

    //红包扫雷
    public static final String RR_RED_SUM = "redEnvelopeSum";                                                           //红包金额： 1:10-300 2:50-500 3:100-1000
    public static final String RR_RED_NUM = "redEnvelopeNum";                                                           //红包个数： 10 9 8 7 6 5
    public static final String RR_RED_MUL = "redEnvelopeMul";                                                           //倍数
    public static final String RR_RED_TIME = "redEnvelopeTime";                                                         //红包扫雷时间： 6s 10s 15s
    public static final String RR_RED_REBRULE= "redRule";                                                               //红包扫雷规则： 1.每人一包 2.可抢多包

    // 打拱
    public static final String RR_ARCH_LAIZI_AS = "archLaizi";                                                          // 赖子规则
    public static final String RR_ARCH_PIAO = "archPiao";                                                               // 飘分
    public static final String RR_ARCH_HUA = "archHua";                                                                 // 花牌

    // 扯拉克
    public static final String RR_CHELAKE_BANKER_TYPE = "mode";                                                         // 玩法，1-轮庄，2-赢者庄，3-混斗罗
    public static final String RR_CHELAKE_LAKE_TYPE = "monsters";                                                       // 拉克牌型组合，0x01-四条，0x02-同花顺，0x04-三顺子，0x08-三同花，0x10-六对半
    public static final String RR_CHELAKE_SPECIAL_RULE = "special";                                                     // 特殊玩法组合，0x01-拉摆对翻，0x02-三敲翻倍
    public static final String RR_CHELAKE_SORT_TIME = "sortTime";                                                       // 理牌时间，单位秒，有效值：60、90、120

    //干瞪眼
    public static final String RR_GDY_DEAL_RULE = "gdyDealRule";                                                        // 干瞪眼 摸牌规则
    public static final String RR_GDY_BANK_RULE = "gdyBankRule";                                                        // 玩家做庄
    public static final String RR_GDY_POKERNUM_RULE = "gdyPokerNum";                                                    // 干瞪眼 几副牌；
    public static final String RR_GDY_BOMB_RULE = "gdyBomb";                                                            // 炸弹规则
    public static final String RR_GDY_LAIZINUM_RULE = "gdyLaiziNum";                                                    // 癞子数量；
    public static final String RR_GDY_CAPPING_RULE = "capping";                                                         // 倍数封顶

    // 牌九
    public static final String RR_PAIGOW_MULTIPLE = "paiGowMultiple";
    public static final String RR_PAIGOW_GAMEPLAY_PAGE = "gamePage";                                                    // 1-大牌九 2-小牌九 3-加锅牌九
    public static final String RR_PAIGOW_GAMEPLAY_RULE = "gameplay";                                                    // 1-大牌九 2-小牌九
    public static final String RR_PAIGOW_MINBETS_RULE = "minBets";                                                      // 最低下注；
    public static final String RR_PAIGOW_BOTTOMSCORE_RULE = "bottomScore";                                              // 锅底分
    public static final String RR_PAIGOW_BANKERCARD_RULE = "bankerCard";                                                // 庄家亮牌 bankerCard 0-不亮牌 2-亮2张 3-亮三张
    public static final String RR_PAIGOW_DAO_RULE = "dao";                                                              // 道的选择 dao  2-两道杠 3-三道杠
    public static final String RR_PAIGOW_BANKER_RULE = "banker";                                                        // 庄家的选择 1 抢庄， 2 轮流庄  3 霸王庄
    public static final String RR_PAIGOW_SELECTSCORE = "selectScore";                                                   // 牌九选分；
    public static final String RR_PAIGOW_FIXEDSCORE = "fixedScore";                                                     // 1 (1 1 1) 2()
    public static final String RR_PAIGOW_BETS = "bets";                                                                 // 1(每次选分) 2(固定)
    public static final String RR_PAIGOW_PLAYMETHOD = "playMethod";                                                     // 牌型选择
    public static final String RR_PAIGOU_OP_TIMER = "opTimer";                                                          // 操作计时
    public static final String RR_PAIGOU_KEEP_HOT = "keepHot";                                                          // 续锅选择
    public static final String RR_PAIGOU_OP_ORDER = "opOrder";                                                          // 操作流程
    public static final String RR_PAIGOU_ROB_MUL = "robMul";                                                            // 抢庄倍数
    public static final String RR_PAIGOU_ROB_BASE = "base";                                                             // 底分选择
    public static final String RR_PAIGOU_ROB_BASE_MUL = "baseMul";                                                      // 底分倍数
    public static final String RR_PAIGOU_ROB_PUSH_TYPE = "pushType";                                                    // 推注类型
    public static final String RR_PAIGOU_ROB_PUSH_MUL = "pushMul";                                                      // 推注倍数
    public static final String RR_PAIGOW_HOT_CNT = "hotCnt";                                                            // 加锅次数
    public static final String RR_PAIGOW_OP_TIME = "opTime";                                                            // 操作时间(ms)
    public static final String RR_PAIGOW_OP_TYPE = "opType";                                                            // 操作流程, 1: 先发牌后下注, 2: 先下注后发牌
    public static final String RR_PAIGOW_MAX_LOOP = "maxLoop";                                                          // 最大局数, -1: 无限
    public static final String RR_PAIGOW_WIN_SERVICE_CHARGE = "winServiceCharge";                                       // 大赢家抽成
    public static final String RR_PAIGOW_OTHER_SERVICE_CHARGE = "otherServiceCharge";                                   // 其他人抽成
    public static final String RR_PAIGOW_WIN_EXTRA_CONDITION = "winExtraCondition";                            			// 大赢家额外高于分
    public static final String RR_PAIGOW_WIN_EXTRA_SERVICE_CHARGE = "winExtraServiceCharge";                            // 大赢家额外高于分抽成x
    public static final String RR_PAIGOW_WIN_EXTRA_CONDITION_NOT_SERVICE_CHARGE = "winExtraConditionNotServiceCHarge";  // 大赢家分低于x免单
    public static final String RR_PAIGOW_PLAY = "paiGowPlayMethod";  													// 牌九特殊玩法
    public static final String RR_COSTMODEL = "costModel";                                                              // 抽水类型
    public static final String RR_COSTMODEL_VALUE = "costModelValue";                                                   // 抽水
    public static final String RR_PAIGOW_OPNE_CARD_TIME = "openCardTime";                                               // 开牌超时时间设置


    // 填大坑
    public static final String RR_TDK_BASE = "tdkBase";                                                                 // 下注，1-[1,2,3,4,5],2-[2,3,5,8,10],3-[5,10,15,20,25]
    public static final String RR_TDK_POKER = "tdkPoker";                                                               // 用牌，1-都从9起，2-3从J、4从10、5从9起，3-3从10、4从10、5从9起

    // 三公
    public static final String RR_SG_BANKER_TYPE = "cowBankerType";                                                     // 庄类型  1:明牌抢庄 2: 自由抢庄,   3通比玩法 4 三公当庄
    public static final String RR_SG_ROB_BANKER_MULTIPLE = "robBankerMultiple";                                         // 抢庄倍数 1: 1倍, 2: 2倍, 3: 3倍 4: 4倍
    public static final String RR_SG_END_POINT = "cowEndPoint";                                                         // 底分 1: 1/2/3/4/5 2: 2/3/4/8/10 3: 5/10/15/20/25
    public static final String RR_SG_PUSH_NOTE_LIMIT = "pushNoteLimit";                                                 // 推注倍数 0: 无, 3: 3倍 5: 5倍 10: 10倍 20: 20倍
    public static final String RR_SG_PORKER_CARD_NUMBER = "cardNumber";                                                 // 扣牌张数
    public static final String RR_SG_PUSH_NOTE_TYPE = "cowPushNoteType";                                                // 推注类型；3.无，2闲家推注，1 抢庄推注 4 闲家推注 经典
    public static final String RR_SG_CARD_MUL_TYPE = "sgCardMulType";                                                   // 三公牌型翻倍類型 0 不翻倍 1 翻倍類型1 2 翻倍類型2

    // 21点
    public static final String RR_BLACK_JACK_BANKER_TYPE = "bankerType";                                                // 庄家 1-抢庄 2-轮庄 3-霸王庄(竞技场没有)
    public static final String RR_BLACK_JACK_BET_NOTE = "betNote";                                                      // 下注 1-[1,2,3,4,5],2-[2,3,5,8,10],3-[5,10,15,20,25]
    public static final String RR_BLACK_JACK_BJ_TIME = "bjTime";                                                        // 时间 15秒场 30秒场
    public static final String RR_BLACK_JACK_MULTIPLE = "multiple";                                                     // 底分倍数

    // 仙桃麻将
    public static final String RR_XTHH_LAIZI_RULE = "laiZiRule";                                                        //  赖子规则（类型）1(默认) 一赖到底 0 必掷

    // 黄石麻将
    public static final String RR_HSMJ_BAR_TYPE = "barType";                                                            // 类型
    public static final String RR_HSMJ_SCORE_TOP = "scoreTop";                                                          // 分数封顶

    //大冶麻将
    public static final String RR_DYMJ_BAR_TYPE = "barType";
    public static final String RR_MJ_DY_BEGIN_HU_FANG = "beginHuFang";
    public static final String RR_MJ_DY_BAG_TYPE = "bagType";
    // 阳新麻将
    public static final String RR_YXMJ_GANG_GENERAL_CARD = "generalCard";                                               // 做将
    public static final String RR_YXMJ_GANG_BRIDGE_CARD = "bridgeCard";                                                 // 杠牌

    // 梭哈
    public static final String RR_FIVECARDSTUD_BASE = "base";                                                           // 开局底注
    public static final String RR_FIVECARDSTUD_TIMER = "timer";                                                         // 倒计时
    public static final String RR_FIVECARDSTUD_INITIAL_POINTS = "initialPoints";                                        // 初始竞技值/分数
    public static final String RR_FIVECARDSTUD_CARDS = "cards";                                                         // 牌库

    // 疯狂炸金花
    public static final String RR_FOLIE_FGF_PLAY_TYPE = "playType";                                                     // 玩法类型((默认)1:常规、2:短牌)
    public static final String RR_FOLIE_FGF_POCKET_POINTS = "pocketPoints";                                             // 牌局大小、带入竞技值
    public static final String RR_FOLIE_FGF_ACTION_ADD_CHIP = "actionAddChip";                                          // 是否自动补充筹码
    public static final String RR_FOLIE_FGF_COMPARE_MUL = "compareMul";                                                 // 比牌倍数((默认)1:单倍、2:双倍)
    public static final String RR_FOLIE_FGF_TIME_DISCARD = "timeDiscard";                                               // 超时弃牌((默认)30:30S、60:60S、120:120S、0:不弃牌)
    public static final String RR_FOLIE_FGF_COMPARE_RULE = "compareRule";                                               // 比牌规则((默认)1:比大小、2:比花色、3:全比)
    public static final String RR_FOLIE_FGF_LIMIT_LOOP = "limitLoop";                                                   // 轮数上限((默认)15:15轮、20:20轮、25:25轮、30:30轮)
    public static final String RR_FOLIE_FGF_STUFFY_LOOP = "stuffyLoop";                                                 // 闷牌轮数((默认)1:1、2:2、3:3、4:4、5:5、0:不限制)

    // 益阳麻将
    public static final String RR_YYMJ_BIRDS = "birds";                                                                 // 抓鸟
    public static final String RR_YYMJ_FINE = "fine";                                                                   // 罚分
    public static final String RR_YYMJ_MAXTOP = "maxTop";                                                               // 封顶

    // 福州麻将
    public static final String RR_FZMJ_CARD_NUM = "cardNum";                                                            // 牌数(14_庄家14张闲家13张、17_庄家17张闲家16张)
    public static final String RR_FZMJ_FANG_PAO = "fangPao";                                                            // 放炮(1_放炮单赔、2_放炮全赔、3_放炮双倍单赔)
    public static final String RR_FZMJ_PLAY_TYPE = "playType";                                                          // 1表示新的 2，表示老的
    public static final String RR_FZMJ_CARELC_NUM = "carelcNum";                                                          //圈数

}
