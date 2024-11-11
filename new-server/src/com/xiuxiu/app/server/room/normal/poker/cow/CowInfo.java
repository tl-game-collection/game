package com.xiuxiu.app.server.room.normal.poker.cow;

import com.xiuxiu.app.server.room.RoomRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @auther: yuyunfei
 * @date: 2020/1/8 11:19
 * @comment:
 */
public class CowInfo {
    private int laiType = 0;                        // 王癞子类型
    /**
     * 赖子牌
     */
    private byte laiZiCard = -1;
    private int pushNoteType;                       // 推注类型
    private int robBankerMul;                       // 抢庄倍数
    private List<Integer> baseRebet = new ArrayList<>(); // 底分
    private int maxPushNote;                        // 推注限制
    private int cardNum = 0;                        // 扣牌数量
    private int pushBankerType;                     // 上庄类型
    private boolean isQuick = false;                // 快速场
    private boolean doubling = false;               // 下注翻倍
    private int multiple = 1;                       // 翻倍规则

    private boolean notRobNotPush;                  // 不抢庄不可推注
    private boolean isWuHuaPai;                     // 无花牌
    private boolean isTongHuaShun;                  // 同花顺
    private boolean isYiTiaoLong;                   // 一条龙
    private boolean isZhaDanNiu;                    // 炸弹牛
    private boolean isWuXiaoNiu;                    // 五小牛
    private boolean isHuLuNiu;                      // 葫芦牛
    private boolean isJinNiu;                       // 金牛
    private boolean isTongHuaNiu;                   // 同花牛
    private boolean isYinNiu;                       // 银牛
    private boolean isShunZiNiu;                    // 顺子牛

    private int sendCardCount = -1;                 // 发牌次数
    private int firstIndex = 0;                     // 第一个玩家位置
    private int prevBankerIndex = -1;               // 上一把庄索引
    private byte dealCardOkCnt = 0;                 // 明牌抢庄添加是否发头张牌完成
    private boolean isSendRobBanker = false;        // 明牌抢庄 开始抢庄是否已经发给玩家
    private int roundCount = 0;                     // 每对局8就洗牌
    private int prevMaxCardPlayerIndex = -1;        // 上一把最大牌型的玩家index
    private boolean preBankerHas = false;           // 上一把庄家是否有牛；
    private int prevMaxPlayerIndex = -1;            // 上一把最大牛牛牌型的玩家index

    private int bankerType;                         // 庄类型
    private int robLessAreanValue;                  // 限制竞技值

    // *************端火锅
    private int optType;                            // 操作流程, 1: 先发牌几张牌后下注, 2: 先下注后发牌
    private int firstBaseBetPre;                    // 首局底注百分比
    private int nextBaseBetPre;                     // 首局后底注百分比
    private int hotLessNote;                        // 端火锅锅底筹码
    private int hotLessLoop;                        // 端火锅 最少几轮
    private int hotMaxLoop;                         // 一庄最大局数
    private int hotLevelLessNote;                   // 手动下庄最低分
    private int hotCnt;                             // 连庄次数
    private int hotUp;                              // 连庄锅底倍数

    private int keepCount = 0;                      // 当前庄续了几次
    private int curHotBankerLoop = 0;               // 当前庄已经进行了多少轮
    private int curHotDeskNote;                     // 当前桌面端火锅已经总筹码
    private boolean isHotOutTip;                    // 是否提示下庄
    private int totalLoop = 0;                      // 总局数


    public void init(HashMap<String, Integer> getRule) {
        this.setWuHuaPai(0 != (getRule.getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.WU_HUA_PAI.getValue()));
        this.setTongHuaShun(0 != (getRule.getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.TONG_HUA_SHUN.getValue()));
        this.setYiTiaoLong(0 != (getRule.getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.YI_TIAO_LONG.getValue()));
        this.setZhaDanNiu(0 != (getRule.getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.ZHA_DAN_NIU.getValue()));
        this.setWuXiaoNiu(0 != (getRule.getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.WU_XIAO_NIU.getValue()));
        this.setHuLuNiu(0 != (getRule.getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.HU_LU_NIU.getValue()));
        this.setJinNiu(0 != (getRule.getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.JIN_NIU.getValue()));
        this.setTongHuaNiu(0 != (getRule.getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.TONG_HUA_NIU.getValue()));
        this.setYinNiu(0 != (getRule.getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.YIN_NIU.getValue()));
        this.setShunZiNiu(0 != (getRule.getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.SHUN_ZI_NIU.getValue()));
        this.setNotRobNotPush(0 != (getRule.getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.BU_QIANG_ZHUANG_BU_KE_TUI_ZHU.getValue()));
        this.setQuick(0 != (getRule.getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.KUAI_SHU.getValue()));
        this.setDoubling(0 != (getRule.getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.XIA_ZHU_FAN_BEI.getValue()));
        this.setBankerType(getRule.getOrDefault(RoomRule.RR_COW_BANKER_TYPE, 1));
        this.setLaiType(getRule.getOrDefault(RoomRule.RR_COW_KING_RAZZ_TYPE, 1));
        this.setPushNoteType(getRule.getOrDefault(RoomRule.RR_COW_PUSH_NOTE_TYPE, 0));
        this.setRobBankerMul(getRule.getOrDefault(RoomRule.RR_COW_ROB_BANKER_MULTIPLE, 1));
        int cowEndPoint = getRule.getOrDefault(RoomRule.RR_COW_END_POINT, 1);
        for (int i = 0; i < 3; i++) {
            int value = cowEndPoint >> (i * 10) & 0X3ff;
            if (value > 0) {
                this.baseRebet.add(value * getRule.getOrDefault(RoomRule.RR_COW_END_POINT_MUL, 10));
            } else {
                break;
            }
        }
        this.setMaxPushNote(getRule.getOrDefault(RoomRule.RR_COW_PUSH_NOTE_LIMIT, 0) * this.getBaseMinRebet());
        this.setCardNum(getRule.getOrDefault(RoomRule.RR_COW_PORKER_CARD_NUMBER, 0));
        this.setPushBankerType(getRule.getOrDefault(RoomRule.RR_COW_PUSH_BANKER_TYPE, 1));
        this.setHotLessNote(getRule.getOrDefault(RoomRule.RR_COW_PORKER_CARD_HOT_NOTE, 10));
        this.setMultiple(getRule.getOrDefault(RoomRule.RR_COW_MULTIPLE, 1));
        this.setHotLessLoop(getRule.getOrDefault(RoomRule.RR_COW_PORKER_CARD_HOT_LOOP, 1));
        this.setRobLessAreanValue(getRule.getOrDefault(RoomRule.RR_DEPUTY_DIVIDE, 100));
        this.setFirstBaseBetPre(getRule.getOrDefault(RoomRule.RR_COW_FIRST_REBET_PRE,10));
        this.setNextBaseBetPre(getRule.getOrDefault(RoomRule.RR_COW_NEXT_REBET_PRE,10));
        this.setHotCnt(getRule.getOrDefault(RoomRule.RR_COW_HOT_CNT, 0));
        this.setOptType(getRule.getOrDefault(RoomRule.RR_COW_OP_TYPE, 1));
        this.setHotLevelLessNote(getRule.getOrDefault(RoomRule.RR_COW_LEVEL_LESS_NOTE, 0));
        this.setHotMaxLoop(getRule.getOrDefault(RoomRule.RR_COW_HOT_MAX_LOOP, 10));
        this.setHotUp(getRule.getOrDefault(RoomRule.RR_COW_HOT_UP, 1));
        this.setHotOutTip(0 != (getRule.getOrDefault(RoomRule.RR_PLAY, 0) & ECowPlayRule.HOT_OUT_TIP.getValue()));
        
        // 明牌抢庄固定 发4张；
        if (ECowPlayTypes.MP_ROB_BANKER.ordinal() == this.getBankerType()) {
            this.setCardNum(4);
        }
    }

    public int getBankerType() {
        return bankerType;
    }

    public void setBankerType(int bankerType) {
        this.bankerType = bankerType;
    }

    public int getLaiType() {
        return laiType;
    }

    public void setLaiType(int laiType) {
        this.laiType = laiType;
    }

    public int getPushNoteType() {
        return pushNoteType;
    }

    public void setPushNoteType(int pushNoteType) {
        this.pushNoteType = pushNoteType;
    }

    public int getRobBankerMul() {
        return robBankerMul;
    }

    public void setRobBankerMul(int robBankerMul) {
        this.robBankerMul = robBankerMul;
    }

    public int getBaseMinRebet() {
        return baseRebet.size() > 0 ? baseRebet.get(0) : 0;
    }

    public List<Integer> getBaseReb(){
        return baseRebet;
    }

    public int getMaxPushNote() {
        return maxPushNote;
    }

    public void setMaxPushNote(int maxPushNote) {
        this.maxPushNote = maxPushNote;
    }

    public int getCardNum() {
        return cardNum;
    }

    public void setCardNum(int cardNum) {
        this.cardNum = cardNum;
    }

    public int getPushBankerType() {
        return pushBankerType;
    }

    public void setPushBankerType(int pushBankerType) {
        this.pushBankerType = pushBankerType;
    }

    public boolean isQuick() {
        return isQuick;
    }

    public void setQuick(boolean quick) {
        isQuick = quick;
    }

    public boolean isDoubling() {
        return doubling;
    }

    public void setDoubling(boolean doubling) {
        this.doubling = doubling;
    }

    public int getHotLessNote() {
        return hotLessNote;
    }

    public void setHotLessNote(int hotLessNote) {
        this.hotLessNote = hotLessNote;
    }

    public int getMultiple() {
        return multiple;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;
    }

    public int getHotLessLoop() {
        return hotLessLoop;
    }

    public void setHotLessLoop(int hotLessLoop) {
        this.hotLessLoop = hotLessLoop;
    }

    public int getHotCnt() {
        return hotCnt;
    }

    public void setHotCnt(int hotCnt) {
        this.hotCnt = hotCnt;
    }

    public int getRobLessAreanValue() {
        return robLessAreanValue;
    }

    public void setRobLessAreanValue(int robLessAreanValue) {
        this.robLessAreanValue = robLessAreanValue;
    }

    public boolean isNotRobNotPush() {
        return notRobNotPush;
    }

    public void setNotRobNotPush(boolean notRobNotPush) {
        this.notRobNotPush = notRobNotPush;
    }

    public boolean isTongHuaShun() {
        return isTongHuaShun;
    }

    public void setTongHuaShun(boolean tongHuaShun) {
        isTongHuaShun = tongHuaShun;
    }

    public boolean isYiTiaoLong() {
        return isYiTiaoLong;
    }

    public void setYiTiaoLong(boolean yiTiaoLong) {
        isYiTiaoLong = yiTiaoLong;
    }

    public boolean isZhaDanNiu() {
        return isZhaDanNiu;
    }

    public void setZhaDanNiu(boolean zhaDanNiu) {
        isZhaDanNiu = zhaDanNiu;
    }

    public boolean isWuXiaoNiu() {
        return isWuXiaoNiu;
    }

    public void setWuXiaoNiu(boolean wuXiaoNiu) {
        isWuXiaoNiu = wuXiaoNiu;
    }

    public boolean isHuLuNiu() {
        return isHuLuNiu;
    }

    public void setHuLuNiu(boolean huLuNiu) {
        isHuLuNiu = huLuNiu;
    }

    public boolean isJinNiu() {
        return isJinNiu;
    }

    public void setJinNiu(boolean jinNiu) {
        isJinNiu = jinNiu;
    }

    public boolean isTongHuaNiu() {
        return isTongHuaNiu;
    }

    public void setTongHuaNiu(boolean tongHuaNiu) {
        isTongHuaNiu = tongHuaNiu;
    }

    public boolean isYinNiu() {
        return isYinNiu;
    }

    public void setYinNiu(boolean yinNiu) {
        isYinNiu = yinNiu;
    }

    public boolean isShunZiNiu() {
        return isShunZiNiu;
    }

    public void setShunZiNiu(boolean shunZiNiu) {
        isShunZiNiu = shunZiNiu;
    }

    public boolean isWuHuaPai() {
        return isWuHuaPai;
    }

    public void setWuHuaPai(boolean wuHuaPai) {
        isWuHuaPai = wuHuaPai;
    }

    public int getSendCardCount() {
        return sendCardCount;
    }

    public void setSendCardCount(int sendCardCount) {
        this.sendCardCount = sendCardCount;
    }

    public int getFirstIndex() {
        return firstIndex;
    }

    public void setFirstIndex(int firstIndex) {
        this.firstIndex = firstIndex;
    }

    public int getPrevBankerIndex() {
        return prevBankerIndex;
    }

    public void setPrevBankerIndex(int prevBankerIndex) {
        this.prevBankerIndex = prevBankerIndex;
    }

    public int addPrevBankerIndex() {
        return ++prevBankerIndex;
    }

    public byte getDealCardOkCnt() {
        return dealCardOkCnt;
    }

    public void setDealCardOkCnt(byte dealCardOkCnt) {
        this.dealCardOkCnt = dealCardOkCnt;
    }

    public void addDealCardOkCnt() {
        ++this.dealCardOkCnt;
    }

    public boolean isSendRobBanker() {
        return isSendRobBanker;
    }

    public void setSendRobBanker(boolean sendRobBanker) {
        isSendRobBanker = sendRobBanker;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public void setRoundCount(int roundCount) {
        this.roundCount = roundCount;
    }

    public void addRoundCount() {
        this.roundCount++;
    }

    public int getCurHotBankerLoop() {
        return curHotBankerLoop;
    }

    public void setCurHotBankerLoop(int curHotBankerLoop) {
        this.curHotBankerLoop = curHotBankerLoop;
    }

    public void addCurHotBankerLoop() {
        ++this.curHotBankerLoop;
    }

    public int getCurHotDeskNote() {
        return curHotDeskNote;
    }

    public void setCurHotDeskNote(int curHotDeskNote) {
        this.curHotDeskNote = curHotDeskNote;
    }

    public int getPrevMaxCardPlayerIndex() {
        return prevMaxCardPlayerIndex;
    }

    public void setPrevMaxCardPlayerIndex(int prevMaxCardPlayerIndex) {
        this.prevMaxCardPlayerIndex = prevMaxCardPlayerIndex;
    }

    public boolean isPreBankerHas() {
        return preBankerHas;
    }

    public void setPreBankerHas(boolean preBankerHas) {
        this.preBankerHas = preBankerHas;
    }

    public int getPrevMaxPlayerIndex() {
        return prevMaxPlayerIndex;
    }

    public void setPrevMaxPlayerIndex(int prevMaxPlayerIndex) {
        this.prevMaxPlayerIndex = prevMaxPlayerIndex;
    }

    public int getFirstBaseBetPre() {
        return firstBaseBetPre;
    }

    public void setFirstBaseBetPre(int firstBaseBetPre) {
        this.firstBaseBetPre = firstBaseBetPre;
    }

    public int getNextBaseBetPre() {
        return nextBaseBetPre;
    }

    public void setNextBaseBetPre(int nextBaseBetPre) {
        this.nextBaseBetPre = nextBaseBetPre;
    }

    public int getOptType() {
        return optType;
    }

    public void setOptType(int optType) {
        this.optType = optType;
    }

    public int getKeepCount() {
        return keepCount;
    }

    public void setKeepCount(int keepCount) {
        this.keepCount = keepCount;
    }

    public int getHotMaxLoop() {
        return hotMaxLoop;
    }

    public void setHotMaxLoop(int hotMaxLoop) {
        this.hotMaxLoop = hotMaxLoop;
    }

    public int getHotLevelLessNote() {
        return hotLevelLessNote;
    }

    public void setHotLevelLessNote(int hotLevelLessNote) {
        this.hotLevelLessNote = hotLevelLessNote;
    }

    public int getHotUp() {
        return hotUp;
    }

    public void setHotUp(int hotUp) {
        this.hotUp = hotUp;
    }

    public boolean isHotOutTip() {
        return isHotOutTip;
    }
    public int getTotalLoop() {
        return totalLoop;
    }

    public void setTotalLoop(int totalLoop) {
        this.totalLoop = totalLoop;
    }

    public void setHotOutTip(boolean hotOutTip) {
        isHotOutTip = hotOutTip;
    }

    public byte getLaiZiCard() {
        return laiZiCard;
    }

    public void setLaiZiCard(byte laiZiCard) {
        this.laiZiCard = laiZiCard;
    }

    @Override
    public String toString() {
        return "CowInfo{" +
                "bankerType=" + bankerType +
                ", laiType=" + laiType +
                ", pushNoteType=" + pushNoteType +
                ", robBankerMul=" + robBankerMul +
                ", baseRebet=" + baseRebet +
                ", maxPushNote=" + maxPushNote +
                ", cardNum=" + cardNum +
                ", pushBankerType=" + pushBankerType +
                ", isQuick=" + isQuick +
                ", doubling=" + doubling +
                ", hotLessNote=" + hotLessNote +
                ", multiple=" + multiple +
                ", hotLessLoop=" + hotLessLoop +
                ", robLessAreanValue=" + robLessAreanValue +
                ", notRobNotPush=" + notRobNotPush +
                ", isWuHuaPai=" + isWuHuaPai +
                ", isTongHuaShun=" + isTongHuaShun +
                ", isYiTiaoLong=" + isYiTiaoLong +
                ", isZhaDanNiu=" + isZhaDanNiu +
                ", isWuXiaoNiu=" + isWuXiaoNiu +
                ", isHuLuNiu=" + isHuLuNiu +
                ", isJinNiu=" + isJinNiu +
                ", isTongHuaNiu=" + isTongHuaNiu +
                ", isYinNiu=" + isYinNiu +
                ", isShunZiNiu=" + isShunZiNiu +
                ", sendCardCount=" + sendCardCount +
                ", firstIndex=" + firstIndex +
                ", prevBankerIndex=" + prevBankerIndex +
                ", dealCardOkCnt=" + dealCardOkCnt +
                ", isSendRobBanker=" + isSendRobBanker +
                ", roundCount=" + roundCount +
                ", curHotBankerLoop=" + curHotBankerLoop +
                ", curHotDeskNote=" + curHotDeskNote +
                ", prevMaxCardPlayerIndex=" + prevMaxCardPlayerIndex +
                ", preBankerHas=" + preBankerHas +
                ", prevMaxPlayerIndex=" + prevMaxPlayerIndex +
                '}';
    }
}
