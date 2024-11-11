package com.xiuxiu.app.server.room.player.mahjong;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.EState;
import com.xiuxiu.app.server.room.normal.RoomPlayer;
import com.xiuxiu.app.server.room.normal.mahjong.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MahjongPlayer extends RoomPlayer implements IMahjongPlayer {
    protected byte[] handCardCnt = new byte[MahjongConstant.MJ_CARD_KINDS];                                             // 手牌
    protected LinkedList<Byte> deskCards = new LinkedList<>();                                                          // 桌上的牌

    protected LinkedList<Byte> laiZiAndPiCard = new LinkedList<>();                         // 桌上癞子和痞子的牌
    protected byte[] pgCardCnt = new byte[MahjongConstant.MJ_CARD_KINDS];                   // 碰杠牌
    protected byte[] brightCardCnt = new byte[MahjongConstant.MJ_CARD_KINDS];               // 亮牌
    protected LinkedList<MahjongCardNode> cpgCards = new LinkedList<>();                    // 吃碰杠牌
    protected LinkedList<Byte> shuaiPai = new LinkedList<>();                               // 甩牌

    protected boolean isBright = false;                                                     // 是否亮牌
    protected byte[] kouCard = new byte[MahjongConstant.MJ_CARD_KINDS];                     // 扣牌

    protected HuInfo huInfo = new HuInfo();                                                 // 胡信息

    protected boolean isLouHu = false;                                                      // 漏胡
    protected int louHuFang = 0;                                                            // 漏胡倍数

    protected int tingFang = 0;                                                             // 听牌最大番数

    protected BrightInfo brightInfo;                                                        // 亮牌信息
    protected HalfBrightInfo halfBrightInfo;                                                // 听牌信息


    protected int piaoScore = 0;                                                            // 选择飘
    protected List<Integer> shuKanPoint = new ArrayList<>()  ;                                                       // 数坎点数1-9
    protected int dingQueColor = -1;                                                        // 定缺颜色

    public MahjongPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    @Override
    public byte[] getHandCard() {
        return this.handCardCnt;
    }

    @Override
    public void addHandCardTo(List<Byte> list) {
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            int len = this.handCardCnt[i];
            if (len < 1) {
                continue;
            }
            for (int j = 0; j < len; ++j) {
                list.add((byte) i);
            }
        }
    }

    @Override
    public void addHandCardTo(List<Byte> list, byte fumble) {
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            int len = this.handCardCnt[i];
            if (len < 1) {
                continue;
            }
            if (i == fumble) {
                --len;
            }
            for (int j = 0; j < len; ++j) {
                list.add((byte) i);
            }
        }
    }

    @Override
    public void addBrightCardTo(List<Byte> list) {
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            int len = this.brightCardCnt[i];
            if (len < 1) {
                continue;
            }
            for (int j = 0; j < len; ++j) {
                list.add((byte) i);
            }
        }
    }

    @Override
    public void addKouCardTo(List<Byte> list) {
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            int len = this.kouCard[i];
            if (len < 1) {
                continue;
            }
            for (int j = 0; j < len; ++j) {
                list.add((byte) i);
            }
        }
    }

    public void addHandCard(byte card) {
        ++this.handCardCnt[card];
    }

    public void delHandCard(byte card, int cnt) {
        this.handCardCnt[card] -= cnt;
    }

    public void addShuaiPai(byte card) {
        this.shuaiPai.addLast(card);
    }

    public boolean hasColor(int color) {
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (this.handCardCnt[i] < 1) {
                continue;
            }
            if (color == MahjongUtil.getColor((byte) i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public byte getCardIndex(byte card) {
        byte cnt = 0;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (i == card) {
                return (byte) (cnt + 1);
            }
            if (this.brightCardCnt[i] > 0) {
                continue;
            }
            cnt += this.handCardCnt[i];
        }
        return 0;
    }

    @Override
    public void fumble(byte card) {
        ++this.handCardCnt[card];
    }

    @Override
    public void takeCard(byte card) {
        --this.handCardCnt[card];
        this.deskCards.add(card);
    }

    @Override
    public void takeCardWithLaiZiOrPi(byte card) {
        --this.handCardCnt[card];
        this.laiZiAndPiCard.addLast(card);
    }

    @Override
    public void bumpCard(byte card) {
        this.handCardCnt[card] -= 2;
        this.pgCardCnt[card] = 3;
        this.cpgCards.addLast(new MahjongCardNode(MahjongConstant.MJ_NODE_TYPE_BUMP, card));
    }

    @Override
    public void barCard(byte card) {
        byte cnt = this.handCardCnt[card];
        this.handCardCnt[card] = 0;
    }

    @Override
    public void eatCard(byte card1, byte card2) {

    }

    @Override
    public void clear() {
        super.clear();
        this.huInfo.clear();
        for (int i = 0; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            this.handCardCnt[i] = 0;
            this.pgCardCnt[i] = 0;
            this.kouCard[i] = 0;
            this.brightCardCnt[i] = 0;
        }
        this.deskCards.clear();
        this.cpgCards.clear();
        this.laiZiAndPiCard.clear();
        this.shuaiPai.clear();

        this.isBright = false;
        this.isLouHu = false;
        this.louHuFang = 0;

        this.tingFang = 0;

        this.brightInfo = null;
        this.halfBrightInfo = null;

        this.dingQueColor = -1;
    }

    @Override
    public long getTimeout(long timeout) {
        MahjongRoom room = (MahjongRoom) RoomManager.I.getRoom(this.roomId);
        return room.getTimeout(this, false, false, false);
    }

    @Override
    public boolean isHosting(int timeout) {
        return -1 != timeout && EState.ONLINE != this.state.get();
    }

    public boolean isBright() {
        return this.isBright;
    }

    public void setBright(boolean bright) {
        this.isBright = bright;
    }

    public HuInfo getHuInfo() {
        return huInfo;
    }

    public boolean isLouHu() {
        return isLouHu;
    }

    public void setLouHu(boolean value) {
        this.isLouHu = value;
    }

    public int getLouHuFang() {
        return louHuFang;
    }

    public void setLouHuFang(int value) {
        this.louHuFang = value;
    }

    public byte[] getPgCardCnt() {
        return this.pgCardCnt;
    }

    public LinkedList<MahjongCardNode> getCpgCards() {
        return cpgCards;
    }

    public int getPiaoScore() {
        return piaoScore;
    }

    public void setPiaoScore(int piaoScore) {
        this.piaoScore = piaoScore;
    }

    public int getTingFang() {
        return tingFang;
    }

    public void setTingFang(int tingFang) {
        this.tingFang = tingFang;
    }

    public BrightInfo getBrightInfo() {
        return brightInfo;
    }

    public void setBrightInfo(BrightInfo brightInfo) {
        this.brightInfo = brightInfo;
    }

    public HalfBrightInfo getHalfBrightInfo() {
        return halfBrightInfo;
    }

    public void setHalfBrightInfo(HalfBrightInfo halfBrightInfo) {
        this.halfBrightInfo = halfBrightInfo;
    }

    public byte[] getKouCard() {
        return kouCard;
    }

    public byte[] getBrightCardCnt() {
        return brightCardCnt;
    }

    public LinkedList<Byte> getDeskCards() {
        return deskCards;
    }

    public List<Byte> getLaiZiAndPiCard() {
        return laiZiAndPiCard;
    }

    public List<Integer> getShuKanPoint() {
        return shuKanPoint;
    }

    public void setShuKanPoint(List<Integer> shuKanPoint) {
        this.shuKanPoint = shuKanPoint;
    }

    public int getDingQueColor() {
        return dingQueColor;
    }

    public void setDingQueColor(int dingQueColor) {
        this.dingQueColor = dingQueColor;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MahjongPlayer::[Player:" + this.playerUid + "\n");
        sb.append("State:" + this.state.get() + "\n");
        sb.append("Index:" + this.index + "\n");
        sb.append("Card:" + MahjongUtils.card2Debug(this.handCardCnt) + "\n");
        sb.append("DeskCard:" + MahjongUtils.card2Debug(this.deskCards) + "\n");
        sb.append("CPGCard:" + MahjongUtils.card2Debug(this.pgCardCnt) + "\n");
        sb.append("BrightCard:" + MahjongUtils.card2Debug(this.brightCardCnt) + "\n");
        sb.append("ShuaiPai:" + MahjongUtils.card2Debug(this.shuaiPai) + "\n");
        sb.append("定缺颜色:" + this.dingQueColor + "\n");
        sb.append("杠:" + this.getScore(Score.MJ_CUR_GANG_SCORE, false) + "\n");
        sb.append("飘:" + this.getScore(Score.MJ_CUR_PIAO_SCORE, false) + "\n");
        sb.append("马:" + this.getScore(Score.MJ_CUR_HORSE_SCORE, false) + "\n");
        sb.append("番:" + this.getScore(Score.MJ_CUR_FANG_SCORE, false) + "\n");
        sb.append("跑恰模八:" + this.getScore(Score.MJ_CUR_PQMB_SCORE, false) + "\n");
        sb.append("数坎:" + this.getScore(Score.MJ_CUR_SHU_KAN_SCORE, false) + "\n");
        sb.append("本轮分数:" + this.getScore(Score.SCORE, false) + "\n");
        sb.append("总分数:" + this.getScore(Score.ACC_TOTAL_SCORE, true) + "\n");
        sb.append("暗杠次数:" + this.getScore(Score.ACC_MJ_AN_GANG_CNT, true) + "\n");
        sb.append("明杠次数:" + this.getScore(Score.ACC_MJ_MING_GANG_CNT, true) + "\n");
        sb.append("自摸次数:" + this.getScore(Score.ACC_MJ_ZIMO_CNT, true) + "\n");
        sb.append("胡次数:" + this.getScore(Score.ACC_MJ_HU_CNT, true) + "\n");
        sb.append("放炮次数:" + this.getScore(Score.ACC_MJ_DIAN_PAO_CNT, true) + "\n");
        sb.append("杠上胡:" + this.huInfo.isGangShangHu + "\n");
        sb.append("杠上炮:" + this.huInfo.isGangShangPao + "\n");
        sb.append("抢杠胡:" + this.huInfo.isQiangGangHu + "\n");
        sb.append("牌型:" + this.huInfo.getPaiXing() + "\n");
        return sb.toString();
    }
}
