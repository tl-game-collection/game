package com.xiuxiu.app.server.room.player.mahjong2;

import java.util.List;

import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.CPGNode;
import com.xiuxiu.app.server.room.normal.mahjong2.EPaiXing;
import com.xiuxiu.app.server.room.normal.mahjong2.EShowFlag;
import com.xiuxiu.app.server.room.normal.mahjong2.HuInfo;
import com.xiuxiu.app.server.room.normal.mahjong2.TingInfo;

public interface IMahjongPlayer extends IRoomPlayer {
    /**
     * 添加手牌
     * 
     * @param card
     */
    void addHandCard(byte card);

    /**
     * 添加手牌
     * 
     * @param card
     * @param cnt
     */
    void addHandCard(byte card, int cnt);

    /**
     * 删除手牌
     * 
     * @param card
     */
    void delHandCard(byte card);

    /**
     * 删除手牌非card
     * 
     * @param card
     */
    byte delHandCardNoCard(byte card);

    /**
     * 删除手牌
     * 
     * @param card
     * @param cnt
     */
    void delHandCard(byte card, int cnt);

    /**
     * 删除card所有手牌
     * 
     * @param card
     */
    void delAllHandCard(byte card);

    /**
     * 判断手牌
     * 
     * @param card
     * @param cnt
     * @return
     */
    boolean hasHandCard(byte card, int cnt);

    /**
     * 判断手牌
     * 
     * @param cards
     * @return
     */
    boolean hasHandCard(byte... cards);

    /**
     * 判断手牌是否含有其他颜色的牌
     * 
     * @param color
     * @return
     */
    boolean hasHandCardWithColor(int color);

    /**
     * 判断手牌是否除258以外的牌
     * 
     * @return
     */
    boolean hasHandCardWithout258();

    /**
     * 判断手牌是否除258以外的牌
     * 
     * @param laiZi
     * @return
     */
    boolean hasHandCardWithout258(byte laiZi);

    /**
     * 获取手牌数量
     * 
     * @param card
     * @return
     */
    int getHandCardCnt(byte card);

    /**
     * 获取手牌数量
     * 
     * @return
     */
    int getHandCardCnt();

    /**
     * 获取手牌中的索引
     * 
     * @param card
     * @return
     */
    byte getHandCardIndex(byte card);

    /**
     * 获取最小的同种牌
     * 
     * @param cnt
     * @return
     */
    int getMinSameColorCard(int cnt);

    /**
     * 获取手牌原始数据, 仅限于胡牌是使用
     * 
     * @return
     */
    byte[] getHandCardRaw();

    /**
     * 获取最后一张手牌
     * 
     * @return
     */
    byte getLastHandCard();

    /**
     * 添加手牌到目标
     * 
     * @param toHandCard
     */
    void addHandCardTo(List<Byte> toHandCard);

    /**
     * 添加手牌到目标
     * 
     * @param toHandCard
     * @param without
     */
    void addHandCardTo(List<Byte> toHandCard, byte without);

    /**
     * 添加牌桌上牌到目标
     * 
     * @param toDeskCard
     */
    void addDeskCardTo(List<Byte> toDeskCard);

    /**
     * 摸牌
     * 
     * @param card
     */
    void fumbleCard(byte card);

    /**
     * 获取最后摸到的牌
     * 
     * @return
     */
    byte getLastFumbleCard();

    /**
     * 获取摸牌次数
     * 
     * @return
     */
    int getFumbleCnt();

    /**
     * 打牌
     * 
     * @param card
     */
    void takeCard(byte card);

    /**
     * 打牌, 非手牌
     * 
     * @param card
     */
    void takeCardWithoutHand(byte card);

    /**
     * 自摸打牌
     * 
     * @param card
     */
    void takeCardByZiMo(byte card);

    /**
     * 获取最后打出去的牌
     * 
     * @return
     */
    byte getLastTakeCard();

    /**
     * 获取第一次胡打出去的牌
     * 
     * @return
     */
    byte getFirstHuTakeCard();

    /**
     * 被别人吃,碰,杠拿走的牌
     * 
     * @param card
     */
    void delDeskLastCard(byte card);

    /**
     * 添加吃碰杠
     * 
     * @param takePlayerIndex
     * @param type
     * @param cardValue
     * @return
     */
    CPGNode addCPG(int takePlayerIndex, CPGNode.EType type, byte cardValue);

    /**
     * 添加任意三张牌到吃碰杠
     * 
     * @param takePlayerIndex
     * @param card1
     * @param card2
     * @param card3
     */
    void addCPGWithAnyThree(int takePlayerIndex, byte card1, byte card2, byte card3);

    /**
     * 添加任意三张类型到目标
     * 
     * @param toAnyThreeType
     */
    void addCPGAnyThreeTypeTo(List<Byte> toAnyThreeType);

    /**
     * 获取吃碰杠列表
     * 
     * @return
     */
    List<CPGNode> getCPGNode();

    /**
     * 获取吃碰杠次数
     * 
     * @return
     */
    int getCPGNodeCnt();

    /**
     * 设置临时吃碰杠次数
     * 
     * @param value
     */
    void setTempCpgNodeCnt(int value);

    /**
     * 获取吃碰杠次数根据类型
     * 
     * @param types
     * @return
     */
    int getCPGNodeCntWithType(CPGNode.EType... types);

    /**
     * 获取吃碰杠次数根据类型除外
     * 
     * @param types
     * @return
     */
    int getCPGNodeCntWithOutType(CPGNode.EType... types);

    /**
     * 是否有碰
     * 
     * @param cardValue
     * @return
     */
    boolean hasBump(byte cardValue);

    /**
     * 碰转为杠
     * 
     * @param cardValue
     * @param type
     */
    void setBumpToBar(byte cardValue, CPGNode.EType type);

    /**
     * 获取听信息
     * 
     * @return
     */
    TingInfo getTingInfo();

    /**
     * 添加胡信息
     * 
     * @param px
     * @param huCard
     */
    void addHu(EPaiXing px, byte huCard);

    /**
     * 添加胡信息
     * 
     * @param takePlayerUid
     * @param px
     * @param huCard
     */
    void addHu(long takePlayerUid, EPaiXing px, byte huCard);

    /**
     * 添加胡信息
     * 
     * @param px
     * @param huCard
     * @param value
     */
    void addHu(EPaiXing px, byte huCard, int value);

    /**
     * 添加胡信息
     * 
     * @param takePlayerUid
     * @param px
     * @param huCard
     * @param value
     */
    void addHu(long takePlayerUid, EPaiXing px, byte huCard, int value);

    /**
     * 获取胡列表
     * 
     * @return
     */
    List<HuInfo> getHuList();

    /**
     * 添加胡牌
     * 
     * @param huCard
     */
    void addHuCard(byte huCard);

    /**
     * 添加胡牌类型到目标
     * 
     * @param list
     */
    void addHuCardTo(List<Byte> list);

    /**
     * 添加漏
     * 
     * @param op
     * @param passCard
     */
    void addPassCard(EActionOp op, byte passCard);

    /**
     * 清理漏
     */
    void clearPassCard();

    /**
     * 是否漏
     * 
     * @param op
     * @param card
     * @return
     */
    boolean isPassCard(EActionOp op, byte card);

    /**
     * 是否漏胡
     * 
     * @param op
     * @return
     */
    boolean isPass(EActionOp op);

    /**
     * 是否胡过了
     * 
     * @return
     */
    boolean isHu();

    /**
     * 是否可以手动出牌
     * 
     * @return
     */
    boolean canManualTake();
    
    void setManualTake(boolean flag);

    /**
     * 清理牌型
     */
    void clearPaiXing();

    /**
     * 添加牌型
     * 
     * @param px
     */
    void addPaiXing(EPaiXing px);

    /**
     * 添加所有牌型
     * 
     * @param px
     */
    void addAllPaiXing(List<EPaiXing> px);

    /**
     * 是否有牌型
     * 
     * @param px
     * @return
     */
    boolean hasPaiXing(EPaiXing px);

    /**
     * 获取牌型
     * 
     * @return
     */
    List<EPaiXing> getAllPaiXing();

    /**
     * 添加显示标识
     * 
     * @param flag
     */
    void addShowFlag(EShowFlag flag);

    /**
     * 移除显示标识
     * 
     * @param flag
     */
    void delShowFlag(EShowFlag flag);

    /**
     * 获取所有显示标识
     * 
     * @return
     */
    List<EShowFlag> getAllShowFlag();

    /**
     * 自动打
     * 
     * @return
     */
    default boolean isAutoTake() {
        return false;
    }

    default boolean isHasTing() {
        return false;
    }

    void setHasTing(boolean hasTing);
}
