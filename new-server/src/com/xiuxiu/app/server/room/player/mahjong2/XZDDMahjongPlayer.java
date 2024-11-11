package com.xiuxiu.app.server.room.player.mahjong2;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.room.normal.mahjong2.CPGNode;

public class XZDDMahjongPlayer extends MahjongPlayer implements IXZDDMahjongPlayer, IDingQue {
    protected int queColor = -1;
    protected boolean huaZhu = false;
    protected int huaZhuValue = 0;
    protected boolean chaJiao = false;
    protected int chaJiaoValue = 0;

    public XZDDMahjongPlayer(int gameType, long roomUid, int roomId) {
        super(gameType, roomUid, roomId);
    }

    @Override
    public int setQue(int color) {
        if (-1 == color) {
            //color = MahjongUtil.getColor((byte) this.getMinSameColorCard(1));
            color = this.getLeastColor();
        }
        return this.queColor = color;
    }

    @Override
    public int getQue() {
        return this.queColor;
    }

    @Override
    public boolean isQueCard(byte card) {
        return this.queColor == MahjongUtil.getColor(card);
    }

    @Override
    public boolean hasQueCard() {
        if (-1 == this.queColor) {
            return false;
        }
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (this.handCard[i] < 1) {
                continue;
            }
            if (this.queColor == MahjongUtil.getColor((byte) i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setHuaZhu(boolean value) {
        this.huaZhu = value;
    }

    @Override
    public boolean isHuaZhu() {
        return this.huaZhu;
    }

    @Override
    public void addHuaZhuValue(int value) {
        this.huaZhuValue += value;
    }

    @Override
    public int getHuaZhuValue() {
        return this.huaZhuValue;
    }

    @Override
    public void setChaJiao(boolean value) {
        this.chaJiao = value;
    }

    @Override
    public boolean isChaJiao() {
        return this.chaJiao;
    }

    @Override
    public void addChaJiaoValue(int value) {
        this.chaJiaoValue += value;
    }

    @Override
    public int getChaJiaoValue() {
        return this.chaJiaoValue;
    }

    @Override
    public boolean hasAllCardWithColor(int color){
        //手牌
        for (int i = 1; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            if (this.handCard[i] > 0) {
                if (color == MahjongUtil.getColor((byte) i)) {
                    return true;
                }
            }
        }
        //吃碰杠牌
        for (CPGNode node : this.cpgNodes) {
            if (color == MahjongUtil.getColor(node.getCard1())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        super.clear();
        this.queColor = -1;
        this.huaZhu = false;
        this.huaZhuValue = 0;
        this.chaJiao = false;
        this.chaJiaoValue = 0;
    }

    //获取数量最少的门
    private int getLeastColor(){
        //万1-9 条10-18 筒19-27
        int m_min = 0;//最少的门的牌数量
        int m_wanCnt = 0;//万的数量
        int m_tiaoCnt = 0;//条的数量
        int m_tongCnt = 0;//筒的数量
        //万
        for (int i = 1; i <= 9; i++) {
            if (this.handCard[i] < 1) {
                continue;
            }
            m_wanCnt += this.handCard[i];
        }
        m_min = m_wanCnt;
        //条
        m_tiaoCnt = 0;
        for (int i = 10; i <= 18; i++) {
            if (this.handCard[i] < 1) {
                continue;
            }
            m_tiaoCnt += this.handCard[i];
        }
        m_min = m_min < m_tiaoCnt ? m_min : m_tiaoCnt;
        //筒
        m_tongCnt = 0;
        for (int i = 19; i <= 27; i++) {
            if (this.handCard[i] < 1) {
                continue;
            }
            m_tongCnt += this.handCard[i];
        }
        m_min = m_min < m_tongCnt ? m_min : m_tongCnt;

        if (m_min == m_wanCnt) {
            return 0;
        } else if (m_min == m_tiaoCnt) {
            return 1;
        } else {
            return 2;
        }
    }
}
