package com.xiuxiu.app.server.room.player.mahjong2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.RoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.CPGNode;
import com.xiuxiu.app.server.room.normal.mahjong2.EPaiXing;
import com.xiuxiu.app.server.room.normal.mahjong2.EShowFlag;
import com.xiuxiu.app.server.room.normal.mahjong2.HuInfo;
import com.xiuxiu.app.server.room.normal.mahjong2.TingInfo;

public class MahjongPlayer extends RoomPlayer implements IMahjongPlayer {
	//手牌 43位字节组
    protected byte[] handCard = new byte[MahjongUtil.MJ_CARD_KINDS];
    //桌牌
    protected ArrayList<Byte> deskCard = new ArrayList<>();
    //吃碰杠
    protected ArrayList<CPGNode> cpgNodes = new ArrayList<>();
    //
    protected int tempCpgNodeCnt = 0;
    //胡牌list
    protected ArrayList<Byte> huCardList = new ArrayList<>();
    //操作列表？？
    protected HashMap<EActionOp, HashSet<Byte>> passCardList = new HashMap<>();

    protected ArrayList<HuInfo> huList = new ArrayList<>();

    protected TingInfo tingInfo = new TingInfo();

    protected ArrayList<EPaiXing> paiXing = new ArrayList<>();
    protected ArrayList<EShowFlag> showFlag = new ArrayList<>();

    protected byte lastFumbleCard = -1;
    protected int fumbleCnt = 0;
    protected byte firstHuTakeCard = -1;
    protected byte lastTakeCard = -1;

    protected int allHandCard = 0;

    // 小局内是否听过
    protected boolean hasTing = false;
    private boolean manualTake = true;

    private byte tingCardValue = -1;
    private byte tingCardIndex = -1;
    private byte desktingIndex = -1;// 牌桌上听位置
    
    protected boolean hasPassCard = false;

    // public MahjongPlayer() {
    //
    // }
    //
    // public MahjongPlayer(IPlayer player) {
    // super(player);
    // }
    //
    // public MahjongPlayer(long roomUid, int roomId) {
    // super(roomUid, roomId);
    // }

    public MahjongPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    @Override
    public void addHandCard(byte card) {
        ++this.handCard[card];
        ++this.allHandCard;
    }

    @Override
    public void addHandCard(byte card, int cnt) {
        this.handCard[card] += cnt;
        this.allHandCard += cnt;
    }

    @Override
    public void delHandCard(byte card) {
        this.delHandCard(card, 1);
    }

    @Override
    public byte delHandCardNoCard(byte card) {
        for (int i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (this.handCard[i] > 0 && i != card) {
                --this.handCard[i];
                return (byte)i;
            }
        }
        return -1;
    }

    @Override
    public void delHandCard(byte card, int cnt) {
        if (this.handCard[card] > cnt) {
            this.allHandCard -= cnt;
            this.handCard[card] -= cnt;
        } else {
            this.allHandCard -= this.handCard[card];
            this.handCard[card] = 0;
        }
    }

    @Override
    public void delAllHandCard(byte card) {
        this.allHandCard -= this.handCard[card];
        this.handCard[card] = 0;
    }

    @Override
    public boolean hasHandCard(byte card, int cnt) {
        return this.handCard[card] >= cnt;
    }

    @Override
    public boolean hasHandCard(byte... cards) {
        int index = 0;
        int len = cards.length;
        for (int i = 0; i < len; ++i) {
            if (this.handCard[cards[i]] < 1) {
                break;
            }
            ++index;
            --this.handCard[cards[i]];
        }
        for (int i = 0; i < index; ++i) {
            ++this.handCard[cards[i]];
        }
        return index == len;
    }

    @Override
    public boolean hasHandCardWithColor(int color) {
        for (int i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (this.handCard[i] > 0) {
                if (color == MahjongUtil.getColor((byte)i)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasHandCardWithout258() {
        return hasHandCardWithout258((byte)-1);

    }

    @Override
    public boolean hasHandCardWithout258(byte laiZi) {
        for (int i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (i == laiZi) {
                continue;
            }
            if (this.handCard[i] > 0) {
                if (MahjongUtil.is258((byte)i)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getHandCardCnt(byte card) {
        return this.handCard[card];
    }

    @Override
    public int getHandCardCnt() {
        return this.allHandCard;
    }

    @Override
    public byte getHandCardIndex(byte card) {
        byte index = 0;
        for (int i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (card == i) {
                ++index;
                break;
            }
            index += this.handCard[i];
        }
        return index;
    }

    @Override
    public int getMinSameColorCard(int cnt) {
        int color = -1;
        int colorCnt = 0;
        int minColor = -1;
        int minColorCnt = Integer.MAX_VALUE;
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (this.handCard[i] < 1) {
                continue;
            }
            byte card = (byte)i;
            int tempColor = MahjongUtil.getColor(card);
            if (-1 == color) {
                color = tempColor;
                colorCnt = this.handCard[i];
            } else if (color != tempColor) {
                if (colorCnt < minColorCnt && colorCnt >= cnt) {
                    minColor = color;
                    minColorCnt = colorCnt;
                }
                color = tempColor;
                colorCnt = this.handCard[i];
            } else {
                colorCnt += this.handCard[i];
            }
        }
        if (colorCnt < minColorCnt && colorCnt >= cnt) {
            minColor = color;
            minColorCnt = colorCnt;
        }
        if (-1 == minColor) {
            return -1;
        }
        int card = 0;
        for (int i = 0, len = cnt; i < MahjongUtil.MJ_CARD_KINDS && len > 0; ++i) {
            if (this.handCard[i] < 1) {
                continue;
            }
            byte c = (byte)i;
            int tempColor = MahjongUtil.getColor(c);
            if (minColor == tempColor) {
                int min = this.handCard[i] < len ? this.handCard[i] : len;
                for (int j = 0; j < min; ++j) {
                    card |= c << (6 * (cnt - len + j));
                }
                len -= min;
            }
        }
        return card;
    }

    @Override
    public byte[] getHandCardRaw() {
        return this.handCard;
    }

    @Override
    public byte getLastHandCard() {
        for (int i = MahjongUtil.MJ_CARD_KINDS - 1; i >= 0; --i) {
            if (this.handCard[i] > 0) {
                return (byte)i;
            }
        }
        return -1;
    }

    @Override
    public void addHandCardTo(List<Byte> toHandCard) {
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            for (int j = 0, len = this.handCard[i]; j < len; ++j) {
                toHandCard.add((byte)i);
            }
        }
    }

    @Override
    public void addHandCardTo(List<Byte> toHandCard, byte without) {
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            for (int j = 0, len = this.handCard[i] - (i == without ? 1 : 0); j < len; ++j) {
                toHandCard.add((byte)i);
            }
        }
    }

    @Override
    public void addDeskCardTo(List<Byte> toDeskCard) {
        toDeskCard.addAll(this.deskCard);
    }

    @Override
    public void fumbleCard(byte card) {
        ++this.handCard[card];
        ++this.allHandCard;
        ++this.fumbleCnt;
        this.lastFumbleCard = card;
    }

    @Override
    public byte getLastFumbleCard() {
        return this.lastFumbleCard;
    }

    @Override
    public int getFumbleCnt() {
        return this.fumbleCnt;
    }

    @Override
    public byte getFirstHuTakeCard() {
        return this.firstHuTakeCard;
    }

    @Override
    public void takeCard(byte card) {
        --this.handCard[card];
        --this.allHandCard;
        this.deskCard.add(card);
        this.lastTakeCard = card;
        this.lastFumbleCard = -1;
    }

    @Override
    public void takeCardWithoutHand(byte card) {
        this.deskCard.add(card);
    }

    @Override
    public void takeCardByZiMo(byte card) {
        --this.handCard[card];
        --this.allHandCard;
        this.lastTakeCard = card;
        this.lastFumbleCard = -1;
    }

    @Override
    public byte getLastTakeCard() {
        return this.lastTakeCard;
    }

    @Override
    public void delDeskLastCard(byte card) {
        int index = this.deskCard.lastIndexOf(card);
        if (-1 != index) {
            this.deskCard.remove(index);
        }
    }

    @Override
    public CPGNode addCPG(int takePlayerIndex, CPGNode.EType type, byte cardValue) {
        CPGNode node = new CPGNode(takePlayerIndex, type, cardValue);
        this.cpgNodes.add(node);
        return node;
    }

    @Override
    public void addCPGWithAnyThree(int takePlayerIndex, byte card1, byte card2, byte card3) {
        this.cpgNodes.add(new CPGNode(takePlayerIndex, card1, card2, card3));
    }

    @Override
    public void addCPGAnyThreeTypeTo(List<Byte> toAnyThreeType) {
        for (CPGNode node : this.cpgNodes) {
            if (CPGNode.EType.ANY_THREE == node.getType()) {
                toAnyThreeType.add(node.getCard1());
                toAnyThreeType.add(node.getCard2());
                toAnyThreeType.add(node.getCard3());
            }
        }
    }

    @Override
    public List<CPGNode> getCPGNode() {
        return this.cpgNodes;
    }

    @Override
    public int getCPGNodeCnt() {
        return this.cpgNodes.size() + this.tempCpgNodeCnt;
    }

    @Override
    public void setTempCpgNodeCnt(int tempCpgNodeCnt) {
        this.tempCpgNodeCnt = tempCpgNodeCnt;
    }

    @Override
    public int getCPGNodeCntWithType(CPGNode.EType... types) {
        int cnt = 0;
        for (CPGNode node : this.cpgNodes) {
            for (CPGNode.EType type : types) {
                if (node.getType() == type) {
                    ++cnt;
                    break;
                }
            }
        }
        return cnt;
    }

    @Override
    public int getCPGNodeCntWithOutType(CPGNode.EType... types) {
        int cnt = 0;
        boolean eq = false;
        for (CPGNode node : this.cpgNodes) {
            eq = false;
            for (CPGNode.EType type : types) {
                if (node.getType() == type) {
                    eq = true;
                    break;
                }
            }
            if (!eq) {
                ++cnt;
            }
        }
        return cnt;
    }

    @Override
    public boolean hasBump(byte cardValue) {
        for (CPGNode node : this.cpgNodes) {
            if (CPGNode.EType.BUMP == node.getType() && cardValue == node.getCard1()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setBumpToBar(byte cardValue, CPGNode.EType type) {
        for (CPGNode node : this.cpgNodes) {
            if (CPGNode.EType.BUMP == node.getType() && cardValue == node.getCard1()) {
                node.setType(type);
                return;
            }
        }
    }

    @Override
    public TingInfo getTingInfo() {
        return this.tingInfo;
    }

    @Override
    public void addHu(EPaiXing px, byte huCard) {
        this.addHu(-1, px, huCard, px.getDefaultValue());
    }

    @Override
    public void addHu(long takePlayerUid, EPaiXing px, byte huCard) {
        this.addHu(takePlayerUid, px, huCard, px.getDefaultValue());
    }

    @Override
    public void addHu(EPaiXing px, byte huCard, int value) {
        this.addHu(-1, px, huCard, value);
    }

    @Override
    public void addHu(long takePlayerUid, EPaiXing px, byte huCard, int value) {
        this.huList.add(new HuInfo(takePlayerUid, px, huCard, value));
    }

    @Override
    public List<HuInfo> getHuList() {
        return this.huList;
    }

    @Override
    public void addHuCard(byte huCard) {
        if (this.huCardList.isEmpty()) {
            this.firstHuTakeCard = this.lastTakeCard;
        }
        this.huCardList.add(huCard);
    }

    @Override
    public void addHuCardTo(List<Byte> list) {
        list.addAll(this.huCardList);
    }

    @Override
    public void addPassCard(EActionOp op, byte passCard) {
        HashSet<Byte> temp = this.passCardList.get(op);
        if (null == temp) {
            temp = new HashSet<>();
            this.passCardList.put(op, temp);
            temp = this.passCardList.get(op);
        }
        temp.add(passCard);
        //hasPassCard = hasPassCard || Boolean.TRUE;
    }

    @Override
    public void clearPassCard() {
        this.passCardList.clear();
    }

    @Override
    public boolean isPassCard(EActionOp op, byte huCard) {
        HashSet<Byte> temp = this.passCardList.get(op);
        if (null != temp) {
            return temp.contains(huCard);
        }
        return false;
    }

    @Override
    public boolean isPass(EActionOp op) {
        HashSet<Byte> temp = this.passCardList.get(op);
        if (null != temp) {
            return !temp.isEmpty();
        }
        return false;
    }

    @Override
    public boolean isHu() {
        return this.getScore(Score.MJ_CUR_HU_CNT, false) >= 1;
    }

    @Override
    public boolean canManualTake() {
        return manualTake;
    }

    @Override
    public void setManualTake(boolean manualTake) {
        this.manualTake = manualTake;
    }

    @Override
    public void clearPaiXing() {
        this.paiXing.clear();
    }

    @Override
    public void addPaiXing(EPaiXing px) {
        this.paiXing.add(px);
    }

    @Override
    public void addAllPaiXing(List<EPaiXing> px) {
        this.paiXing.addAll(px);
    }

    @Override
    public boolean hasPaiXing(EPaiXing px) {
        return -1 != this.paiXing.indexOf(px);
    }

    @Override
    public List<EPaiXing> getAllPaiXing() {
        return this.paiXing;
    }

    @Override
    public void addShowFlag(EShowFlag flag) {
        this.showFlag.add(flag);
    }

    @Override
    public void delShowFlag(EShowFlag flag) {
        this.showFlag.remove(flag);
    }

    @Override
    public List<EShowFlag> getAllShowFlag() {
        return this.showFlag;
    }

    @Override
    public void clear() {
        super.clear();
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            this.handCard[i] = 0;
        }
        this.fumbleCnt = 0;
        this.lastFumbleCard = -1;
        this.firstHuTakeCard = -1;
        this.lastTakeCard = -1;
        this.allHandCard = 0;
        this.deskCard.clear();
        this.cpgNodes.clear();
        this.huList.clear();
        this.tingInfo.clear();
        this.huCardList.clear();
        this.passCardList.clear();
        this.paiXing.clear();
        this.showFlag.clear();
        this.tempCpgNodeCnt = 0;
        this.hasTing = false;
        this.manualTake = true;
        this.tingCardValue = -1;
        this.hasTing = false;
        this.manualTake = true;
        
        this.tingCardValue = -1;
        this.tingCardIndex = -1;
        // 牌桌上听位置
        this.desktingIndex = -1;
        this.hasPassCard = false;
    }

    private String getDebugHandCard() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            byte value = this.handCard[i];
            if (value < 1) {
                continue;
            }
            for (int j = 0; j < value; ++j) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(MahjongUtil.getCardStr((byte)i));
                first = false;
            }
        }
        return sb.toString();
    }

    private String getDebugDeskCard() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Byte c : this.deskCard) {
            if (!first) {
                sb.append(",");
            }
            sb.append(MahjongUtil.getCardStr(c));
            first = false;
        }
        return sb.toString();
    }

    private String getDebugCPGCard() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (CPGNode node : this.cpgNodes) {
            if (!first) {
                sb.append(", ");
            }
            if (CPGNode.EType.ANY_THREE == node.getType()) {
                sb.append(
                    String.format("[%s: %s %s %s]", node.getType().getDesc(), MahjongUtil.getCardStr(node.getCard1()),
                        MahjongUtil.getCardStr(node.getCard2()), MahjongUtil.getCardStr(node.getCard3())));
            } else {
                sb.append(String.format("[%s: %s]", node.getType().getDesc(), MahjongUtil.getCardStr(node.getCard1())));
            }
            first = false;
        }
        return sb.toString();
    }

    private String getDebugHuList() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (HuInfo huInfo : this.huList) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(String.format("[胡牌: %s, 牌型: %s, 番分: %d, 自摸: %s]", MahjongUtil.getCardStr(huInfo.getHuCard()),
                huInfo.getPaiXing(), huInfo.getFang(), huInfo.isZiMo()));
            first = false;
        }
        return sb.toString();
    }

    private String getDebugPaiXing() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (EPaiXing paiXing : this.paiXing) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(String.format("[%s]", paiXing.name()));
            first = false;
        }
        return sb.toString();
    }

    private String getDebugShowFlag() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (EShowFlag flag : this.showFlag) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(String.format("[%s]", flag.getDesc()));
            first = false;
        }
        return sb.toString();
    }

    private String getDebugScore() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Integer> entry : this.score.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(String.format("[%s:%d]", entry.getKey(), entry.getValue()));
            first = false;
        }
        return sb.toString();
    }

    private String getDebugAccScore() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Integer> entry : this.accScore.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(String.format("[%s:%d]", entry.getKey(), entry.getValue()));
            first = false;
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        IPlayer player = this.getPlayer();
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("==========================开始================================================\n");
        sb.append(String.format("[PlayerUid: %d, PlayerName: %s, RoomUid: %d, RoomId: %d]\n", player.getUid(),
            player.getName(), this.getRoomUid(), this.getRoomId()));
        sb.append(String.format("手牌: %s\n", this.getDebugHandCard()));
        sb.append(String.format("桌上的牌: %s\n", this.getDebugDeskCard()));
        sb.append(String.format("吃碰杠: %s\n", this.getDebugCPGCard()));
        sb.append(String.format("胡列表: %s\n", this.getDebugHuList()));
        sb.append(String.format("牌型: %s\n", this.getDebugPaiXing()));
        sb.append(String.format("显示标志: %s\n", this.getDebugShowFlag()));
        sb.append(String.format("分数1: %s\n", this.getDebugScore()));
        sb.append(String.format("分数2: %s\n", this.getDebugAccScore()));
        sb.append("==========================结束================================================\n");
        return sb.toString();
    }

    @Override
    public boolean isHasTing() {
        return hasTing;
    }

    @Override
    public void setHasTing(boolean hasTing) {
        this.hasTing = hasTing;
    }

    public byte getTingCardValue() {
        return tingCardValue;
    }

    public void setTingCardValue(byte tingCardValue) {
        this.tingCardValue = tingCardValue;
    }

    public byte getTingCardIndex() {
        return tingCardIndex;
    }

    public void setTingCardIndex(byte tingCardIndex) {
        this.tingCardIndex = tingCardIndex;
    }

    public byte getDesktingIndex() {
        return desktingIndex;
    }

    public void setDesktingIndex(byte desktingIndex) {
        this.desktingIndex = desktingIndex;
    }

    public boolean isHasPassCard() {
        return hasPassCard;
    }

    public void setHasPassCard(boolean hasPassCard) {
        this.hasPassCard = hasPassCard;
    }

}
