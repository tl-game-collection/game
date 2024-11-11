package com.xiuxiu.app.server.room;

public class Score {
    // 通用
	// 通用--总分
    public static final String ACC_TOTAL_SCORE = "totalScore";
    // 通用--分数
    public static final String SCORE = "score";
    public static final String ACC_PERV_SCORE = "prevScore";                                                            // 通用--上一把分数
    public static final String ACC_PREV_WIN = "prevWin";                                                                // 通用--上一把赢
    public static final String ACC_WIN_CNT = "winCnt";                                                                  // 通用--赢的次数
    // 通用--输的次数
    public static final String ACC_LOST_CNT = "lostCnt";
    public static final String ACC_LOST_CNT_CONTINUE = "continueLostCnt";                                               // 通用--连续输的次数
    public static final String ACC_MAX_SCORE = "maxScore";                                                              // 通用--最大分数
    public static final String WIN_SCORE = "winScore";                                                                  // 通用--赢的分数
    public static final String STAKES = "stakes";                                                                       // 通用--筹码数量
    public static final String AUTO_FILL_UP_STAKES = "autoFillUpStakes";                                                // 通用--自动补充筹码到数量

    // 麻将
    public static final String ACC_MJ_ZIMO_CNT = "ziMoCnt";                                                             // 麻将--自摸次数
    public static final String ACC_MJ_HU_CNT = "huCnt";                                                                 // 麻将--胡次数
    public static final String ACC_MJ_DIAN_PAO_CNT = "dianPaoCnt";                                                      // 麻将--点/放炮次数
    public static final String ACC_MJ_AN_GANG_CNT = "anGangCnt";                                                        // 麻将--暗杠次数
    public static final String ACC_MJ_MING_GANG_CNT = "mingGangCnt";                                                    // 麻将--明杠次数
    public static final String MJ_CALC_FANG_SCORE = "mjCalcFangScore";                                                  // 麻将--当前计算牌型分数
    public static final String MJ_CALC_GANG_SCORE = "mjCalcGangScore";                                                  // 麻将--当前计算杠分数
    public static final String MJ_CALC_HORSE_SCORE = "mjCalcHorseScore";                                                // 麻将--当前计算马分
    public static final String MJ_CALC_SHU_KAN_SCORE = "mjCalcShuKanScore";                                             // 麻将--当前计算数坎分数
    public static final String MJ_CALC_PQMB_SCORE = "mjCalcPQMBScore";                                                  // 麻将--当前计算跑恰模八分数
    public static final String MJ_CALC_PIAO_SCORE = "mjCalcPiaoScore";                                                  // 麻将--当前计算飘分
    public static final String MJ_CUR_MING_GANG_CNT = "mjCurMingGangCnt";                                               // 麻将--当前明杠次数
    public static final String MJ_CUR_AN_GANG_CNT = "mjCurAnGangCnt";                                                   // 麻将--当前暗杠次数
    public static final String MJ_CUR_FANG_SCORE = "mjCurFangScore";                                                    // 麻将--当前牌型分数
    public static final String MJ_CUR_GANG_SCORE = "mjCurGangScore";                                                    // 麻将--当前杠分数
    public static final String MJ_CUR_HORSE_SCORE = "mjCurHorseScore";                                                  // 麻将--当前马分数
    public static final String MJ_CUR_SHU_KAN_SCORE = "mjCurShuKanScore";                                               // 麻将--当前数坎分数
    public static final String MJ_CUR_PQMB_SCORE = "mjCurPQMBScore";                                                    // 麻将--当前跑恰模八分数
    public static final String MJ_CUR_PIAO_SCORE = "mjCurPiaoScore";                                                    // 麻将--当前飘分
    public static final String MJ_CUR_NIAO_SCORE = "mjCurNiaoScore";                                                    // 麻将--当前鸟分
    public static final String MJ_CUR_NIAO_HIT_SCORE = "mjCurNiaoHitScore";                                             // 麻将--当前中鸟分
    public static final String MJ_CUR_NIAO_HIT_NUM = "mjCurNiaoHitNum";                                                 // 麻将--当前中鸟个数
    public static final String MJ_CUR_ZENG_SCORE = "mjCurZengScore";                                                    // 麻将--当前增分
    public static final String MJ_CUR_PPH_SCORE = "mjCurPPHScore";                                                      // 麻将--当前碰碰胡得分
    public static final String MJ_CUR_WYPN_SCORE = "mjCurWypnScore";                                                    // 麻将--当前围一票鸟得分
    public static final String MJ_CUR_SEVEN_SCORE = "mjCurSevenScore";                                                  // 麻将--当前七对得分
    public static final String MJ_NEXT_CARD = "mjNextCard";                                                             // 麻将--下一张牌
    public static final String MJ_CUR_HU_CNT = "mjCurHuCnt";                                                            // 麻将--当前胡的次数
    public static final String MJ_CUR_HU_SCORE = "mjCurHuScore";                                                        // 麻将--当前胡分
    public static final String MJ_CUR_ZIMO_CNT = "mjCurZiMoCnt";                                                        // 麻将--当前自摸次数
    public static final String MJ_CUR_CHA_DA_JIAO_SCORE = "mjCurChaDaJiaoScore";                                        // 麻将--查大叫分数
    public static final String MJ_CUR_CHA_HUA_ZHU_SCORE = "mjCurChaHuaZhuScore";                                        // 麻将--查花猪分数
    public static final String MJ_CUR_EXTRA_GANG_SCORE = "mjCurExtraGangScore";                                         // 麻将--当前额外杠分数
    public static final String MJ_CUR_KAI_KOU_CNT = "mjCurKaiKouCnt";                                                   // 麻将--当前开口次数
    public static final String MJ_CUR_TAKE_PI_CNT = "mjCurTakePiCnt";                                                   // 麻将--当前打出皮子数
    public static final String MJ_CUR_TAKE_LAIZI_CNT = "mjCurTakeLaiZiCnt";                                             // 麻将--当前打出癞子数
    public static final String MJ_CUR_HU_TYPE = "mjCurHuType";                                                          // 麻将--当前胡类型
    public static final String MJ_CUR_START_HU_SCORE = "mjCurStartHuScore";                                             // 麻将--当前起手胡分
    public static final String MJ_CUR_BIG_HU_CNT = "mjCurBigHuCnt";                                                     // 麻将--当前大胡次数
    public static final String MJ_CUR_OPEN_BAR_CNT = "mjCurOpenBarCnt";                                                 // 麻将--当前开杠次数

    // 扑克
    public static final String POKER_BOMB_SCORE = "pokerBombScore";                                                     // 扑克--炸弹分数
    public static final String ACC_POKER_BOMB_CNT = "pokerBombCnt";                                                     // 扑克--炸弹次数
    public static final String ACC_POKER_MAX_CARD_TYPE = "maxCardType";                                                 // 扑克--最大牌型
    public static final String POKER_MULTIPLE_VALUE = "pokerMultipleValue";                                             // 扑克--倍数;

    // 跑得快


    // 牛牛
    public static final String POKER_COW_ROB_BANKER_MUL = "cowRobBankerMul";                                            // 牛牛--抢庄倍数
    // 牛牛--下注score
    public static final String POKER_COW_REBET = "cowRebet";                                                            
    public static final String POKER_COW_SCOREDOUBLE = "scoreDouble";                                                   // 牛牛--结算积分倍数
    public static final String ACC_POKER_COW_MAX_REBET = "maxRebet";                                                    // 牛牛--最大
    // 牛牛--当前一小轮分数
    public static final String POKER_COW_LOOP_SCORE = "CowLoopScore";                                                   
    // 扎金花
    public static final String POKER_FGF_NOTE = "fgfNote";                                                              // 扎金花--下注

    //五十K
    public static final String POKER_510K_ROUND_SCORE = "poker510KRoundScore";                                          ////五十K-一轮得分；
    public static final String POKER_510K_SCORE = "poker510KScore";                                                     //五十K-每局牌分

    //德州下注
    public static final String POKER_TEXAS_ON_REBET = "pokerTexasOnRebet";                                              //德州下注分数；
    public static final String POKER_TEXAS_REBET_RESULT = "PokerTexasRebetResult";                                      //每局结算得分；
    public static final String POKER_TEXAS_LOOKTHREEPOKER_CNT = "PokerTexasLookThreePokerCnt";                          //看3张牌的次数
    public static final String POKER_TEXAS_LOOKTHREEPOKER_ADDNOTE_CNT = "PokerTexasLookThreePokerAddNoteCnt";           //看3张牌前加注的次数
    public static final String POKER_TEXAS_ADDNOTE_CNT = "pokerTexasAddNoteCnt";                                        //加注的次数；
    public static final String POKER_TEXAS_ADDNOTE_ONLAST = "PokerTexasAddNoteOnLast";                                  //跟到最后的次数
    public static final String POKER_TEXAS_ALLIN_CNT = "pokerTexasAllinCnt";                                            //all in 的次数
    public static final String POKER_TEXAS_ALLIN_WIN = "PokerTexasAllInWin";                                            //all in 获胜次数；
    public static final String POKER_TEXAS_ALLIN_LOST = "PokerTexasAllInLost";                                          //all in 失败次数；
    public static final String POKER_TEXAS_PLAY_CNT = "pokerTexasPlayCnt";                                              //德州参与的次数
    public static final String POKER_TEXAS_WINMAXSCORE = "PokerTexasWinMaxScore";                                       //德州单次赢得最大
    public static final String POKER_TEXAS_REBET = "pokerTexasRebet";                                                   //德州下注；

    //红包扫雷
    public static final String POKER_RE_SEND_NUM = "pokerRedEnvelopeSendNum";                                           //发红包数量
    public static final String POKER_RE_SEND_SUM = "pokerRedEnvelopeSendSum";                                           //发红包总金额
    public static final String POKER_RE_ROB_NUM = "pokerRedEnvelopeRobNum";                                             //抢红包数量
    public static final String POKER_RE_ROB_SUM = "pokerRedEnvelopeRobSum";                                             //抢红包总金额
    public static final String POKER_RE_WIN_REPARATION = "pokerRedEnvelopeWinReparation";                               //赢红包赔偿
    public static final String POKER_RE_LOST_REPARATION = "pokerRedEnvelopeLostReparation";                             //输红包赔偿
    public static final String POKER_RE_THUNDER_NUM = "pokerRedEnvelopeThunderNum";                                     //中雷次数

    //十三水
    public static final String POKER_THIRTEEN_HEAD_SCORE = "pokerThirteenHeadScore";                                    //头道分数
    public static final String POKER_THIRTEEN_MEDIUM_SCORE = "pokerThirteenMediumScore";                                //中道分数
    public static final String POKER_THIRTEEN_TAIL_SCORE = "pokerThirteenTailScore";                                    //尾道分数
    public static final String POKER_THIRTEEN_HEAD_CARD_SCORE = "pokerThirteenHeadCardScore";                           //头道牌型分数
    public static final String POKER_THIRTEEN_MEDIUM_CARD_SCORE = "pokerThirteenMediumCardScore";                        //中道牌型分数
    public static final String POKER_THIRTEEN_TAIL_CARD_SCORE = "pokerThirteenTailCardScore";                           //尾道牌型分数
    public static final String POKER_THIRTEEN_MONSTER_SCORE = "pokerThirteenMonsterScore";                              //怪物分数
    public static final String POKER_THIRTEEN_SHOOT_SCORE = "pokerThirteenShootScore";                                  //打枪分数
    public static final String POKER_THIRTEEN_ROB_BANKER_MUL = "pokerThirteenRobBankerMul";                             //抢庄分数
    public static final String POKER_THIRTEEN_REBET = "pokerThirteenRebet";                                             //下注

    //大菠萝
    public static final String POKER_DIABLO_ROB_BANKER_MUL = "pokerDiabloRobBankerMul";                                 //大菠萝抢庄分数
    public static final String POKER_DIABLO_REBET = "pokerDiabloRebet";                                                 //下注
    public static final String POKER_DIABLO_SHOOT_SCORE = "pokerDiabloShootScore";                                      //打枪分数
    public static final String POKER_DIABLO_HEAD_SCORE = "pokerDiabloHeadScore";                                    //头道分数
    public static final String POKER_DIABLO_MEDIUM_SCORE = "pokerDiabloMediumScore";                                //中道分数
    public static final String POKER_DIABLO_TAIL_SCORE = "pokerDiabloTailScore";                                    //尾道分数
    public static final String POKER_DIABLO_MONSTER_SCORE = "pokerDiabloMonsterScore";                              //怪物分数


    // 百人
    public static final String HUNDRED_REB_INDEX = "hundredRebIndex";                                                   // 百人下注索引
    public static final String HUNDRED_ALL_REB = "hundredAllReb";                                                       // 百人当前局所有下注

    //牌九
    public static final String POKER_PAIGOW_ONE_REB = "paiGowOneReb";                                                   //牌九第1道下注
    public static final String POKER_PAIGOW_TWO_REB = "paiGowTwoReb";                                                   //牌九第2道下注
    public static final String POKER_PAIGOW_THREE_REB = "paiGowThreeReb";                                               //牌九第3道下注
    public static final String POKER_PAIGOW_LOOP_SCORE = "paiGowLoopScore";                                             //牌九当前轮分数
    public static final String POKER_PAIGOW_ROB_BANKER_MUL = "paiGowRobBankerMul";

    // 三公
    public static final String POKER_SG_ROB_BANKER_MUL = "sgRobBankerMul";                                            // 三公--抢庄倍数
    public static final String POKER_SG_REBET = "sgRebet";                                                            // 三公--下注

    // 21点
    public static final String POKER_BLACK_JACK_ROB_BANKER_MUL = "blackJackRobBankerMul";                               // 21点--抢庄
    public static final String POKER_BLACK_JACK_REBET = "blackJackRebet";                                               // 21点--下注
    public static final String POKER_BLACK_JACK_BUY_INSURANCE = "blackJackBuyInsurance";                                // 21点--保险

    // 字牌
    public static final String WORDCARD_XI_SCORE = "wordCardXiScore";                                                   // 字牌--息数分
    public static final String ACC_WORDCARD_ZIMO_CNT = "ziMoCnt";                                                       // 字牌--自摸次数
    public static final String ACC_WORDCARD_HU_CNT = "huCnt";                                                           // 字牌--胡次数
    public static final String ACC_WORDCARD_DIAN_PAO_CNT = "dianPaoCnt";                                                // 字牌--点/放炮次数
    public static final String ACC_WORDCARD_QILONG_CNT = "qiLongCnt";                                                   // 字牌--起拢次数
    public static final String ACC_WORDCARD_KAIZHAO_CNT = "kaiZhaoCnt";                                                 // 字牌--开朝次数
}
