package com.xiuxiu.app.server.room.normal.mahjong2.whmj;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.GameInfo;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.mahjong2.CPGNode;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@GameInfo(gameType = GameType.GAME_TYPE_WHMJ, gameSubType = 1)
public class KouKouFangWHMJMahjongRoom extends WHMJMahjongRoom {

    public KouKouFangWHMJMahjongRoom(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public KouKouFangWHMJMahjongRoom(RoomInfo info, ERoomType roomType) {
        super(info, roomType);
    }

    @Override
    protected void doDealGoodCards(Map<Integer, LinkedList<Byte>> playerGoodCards) {

    }

    @Override
    protected void doDealAfter() {
        this.fangCard = this.allCard.removeFirst();

        byte pi1Card = this.fangCard;
        byte pi2Card = -1;
        byte pi3Card = -1;

        int color = MahjongUtil.getColor(this.fangCard);
        if (color <= MahjongUtil.COLOR_TONG) {
            // 万, 条, 筒
            byte begin = (byte) (this.fangCard - MahjongUtil.MJ_1_WANG);
            this.laiZiCard = (byte) ((begin / 9) * 9 + ((begin + 1) % 9) + MahjongUtil.MJ_1_WANG);
            pi2Card = (byte) ((begin / 9) * 9 + ((begin - 1 + 9) % 9) + MahjongUtil.MJ_1_WANG);
        } else {
            byte begin = (byte) (this.fangCard - MahjongUtil.MJ_D_FENG);
            this.laiZiCard = (byte) ((begin / 7) * 7 + ((begin + 1) % 7) + MahjongUtil.MJ_D_FENG);
            pi2Card = (byte) ((begin / 7) * 7 + ((begin - 1 + 7) % 7) + MahjongUtil.MJ_D_FENG);

            if (this.fangCard == MahjongUtil.MJ_B_FENG) {
                // 北
                this.laiZiCard = MahjongUtil.MJ_F_FENG;
            } else if (this.fangCard == MahjongUtil.MJ_Z_FENG) {
                // 中
                this.laiZiCard = MahjongUtil.MJ_F_FENG;
                pi1Card = MahjongUtil.MJ_X_FENG;
                pi2Card = MahjongUtil.MJ_B_FENG;
            } else if (this.fangCard == MahjongUtil.MJ_F_FENG) {
                // 发
                pi2Card = MahjongUtil.MJ_B_FENG;
            }
        }

        pi3Card = MahjongUtil.MJ_Z_FENG;

        if (-1 != pi1Card) {
            this.piList.add(pi1Card);
        }
        if (-1 != pi2Card) {
            this.piList.add(pi2Card);
        }
        if (-1 != pi3Card) {
            this.piList.add(pi3Card);
        }
    }

    @Override
    protected int getHuFang(IMahjongPlayer player) {
        int fang = player.getScore(Score.MJ_CUR_KAI_KOU_CNT, false) + player.getScore(Score.MJ_CUR_TAKE_PI_CNT, false) + 2 * player.getScore(Score.MJ_CUR_TAKE_LAIZI_CNT, false);
        List<CPGNode> cpgNodes = player.getCPGNode();
        for (CPGNode node : cpgNodes) {
            if (CPGNode.EType.BAR_PI == node.getType()) {
                // 皮子杠
                ++fang;
            } else if (CPGNode.EType.BAR_LAIZI == node.getType()) {
                // 癞子杠
                fang += 2;
            } else if (CPGNode.EType.BAR_AN == node.getType()) {
                // 暗杠
                fang += 2;
            } else if (CPGNode.EType.BAR_MING == node.getType() || CPGNode.EType.BAR_FANG == node.getType()) {
                // 明杠, 放杠
                ++fang;
            }
        }
        return fang;
    }
}
