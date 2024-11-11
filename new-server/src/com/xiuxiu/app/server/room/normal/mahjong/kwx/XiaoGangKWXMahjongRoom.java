package com.xiuxiu.app.server.room.normal.mahjong.kwx;

import com.xiuxiu.app.server.room.GameInfo;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.mahjong.EHuType;
import com.xiuxiu.app.server.room.normal.mahjong.EShuKanType;
import com.xiuxiu.app.server.room.normal.mahjong.MahjongConstant;
import com.xiuxiu.app.server.room.player.mahjong.MahjongPlayer;

@GameInfo(gameType = GameType.GAME_TYPE_KWX, gameSubType = 1)
public class XiaoGangKWXMahjongRoom extends KWXMahjongRoom {
    public XiaoGangKWXMahjongRoom(RoomInfo info) {
        super(info);
    }

    @Override
    protected void shuKan(MahjongPlayer takeRoomPlayer, MahjongPlayer huRoomPlayer, byte huCard) {
        if (!this.isShuKan()) {
            return;
        }
        if (huRoomPlayer.getUid() != takeRoomPlayer.getUid()) {
            ++huRoomPlayer.getHandCard()[huCard];
        }
        int shuKanModel = this.info.getRule().getOrDefault(RoomRule.RR_MJ_SHU_KAN, 0);
        if (0 != (shuKanModel & EShuKanType.WILL.getValue())) {
            if (huRoomPlayer.getHuInfo().isWillTwoFiveEight) {
                int total = 0;
                if (huRoomPlayer.getHuInfo().isZiMo) {
                    for (int i = 0; i < this.playerNum; ++i) {
                        MahjongPlayer player = (MahjongPlayer) this.allPlayer[i];
                        if (null == player || player.isGuest()) {
                            continue;
                        }
                        if (player.getUid() == huRoomPlayer.getUid()) {
                            continue;
                        }
                        player.addScore(Score.MJ_CALC_SHU_KAN_SCORE, -2, false);
                        huRoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, 2, false);
                        player.getHuInfo().paixing[EHuType.WILL.ordinal()] = -2;
                        total += 2;
                    }
                } else {
                    huRoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, 2, false);
                    takeRoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, -2, false);
                    takeRoomPlayer.getHuInfo().paixing[EHuType.WILL.ordinal()] = -2;
                    total = 2;
                }
                huRoomPlayer.getHuInfo().paixing[EHuType.WILL.ordinal()] = total;
            }
        }
        if (0 != (shuKanModel & EShuKanType.FIRST_POINT.getValue())) {
            int cnt = 0;
            for (int i = 0; i <  huRoomPlayer.getShuKanPoint().size();i++){
                if(huRoomPlayer.getShuKanPoint().get(i)>0) {
                    cnt += huRoomPlayer.getHandCard()[9 + huRoomPlayer.getShuKanPoint().get(i)];
                    cnt += huRoomPlayer.getPgCardCnt()[9 + huRoomPlayer.getShuKanPoint().get(i)];
                    cnt += huRoomPlayer.getHandCard()[18 + huRoomPlayer.getShuKanPoint().get(i)];
                    cnt += huRoomPlayer.getPgCardCnt()[18 + huRoomPlayer.getShuKanPoint().get(i)];
                }
            }

            int total = 0;
            if (huRoomPlayer.getHuInfo().isZiMo) {
                for (int i = 0; i < this.playerNum; ++i) {
                    MahjongPlayer player = (MahjongPlayer) this.allPlayer[i];
                    if (null == player || player.isGuest()) {
                        continue;
                    }
                    if (player.getUid() == huRoomPlayer.getUid()) {
                        continue;
                    }
                    int cnt2 = 0;
                    for (int n = 0; n <  player.getShuKanPoint().size();n++) {
                        if (player.getShuKanPoint().get(n) > 0) {
                            cnt2 += huRoomPlayer.getHandCard()[9 + player.getShuKanPoint().get(n)];
                            cnt2 += huRoomPlayer.getPgCardCnt()[9 + player.getShuKanPoint().get(n)];
                            cnt2 += huRoomPlayer.getHandCard()[18 + player.getShuKanPoint().get(n)];
                            cnt2 += huRoomPlayer.getPgCardCnt()[18 + player.getShuKanPoint().get(n)];
                        }
                    }

                    player.addScore(Score.MJ_CALC_SHU_KAN_SCORE, -cnt - cnt2, false);
                    huRoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, cnt + cnt2, false);
                    player.getHuInfo().paixing[EHuType.POINT.ordinal()] = -cnt - cnt2;
                    total += cnt + cnt2;
                }
            } else {
                int cnt2=0;
                for (int n = 0; n <  takeRoomPlayer.getShuKanPoint().size();n++) {
                    if (takeRoomPlayer.getShuKanPoint().get(n) > 0) {
                        cnt2 += huRoomPlayer.getHandCard()[9 + takeRoomPlayer.getShuKanPoint().get(n)];
                        cnt2 += huRoomPlayer.getPgCardCnt()[9 + takeRoomPlayer.getShuKanPoint().get(n)];
                        cnt2 += huRoomPlayer.getHandCard()[18 + takeRoomPlayer.getShuKanPoint().get(n)];
                        cnt2 += huRoomPlayer.getPgCardCnt()[18 + takeRoomPlayer.getShuKanPoint().get(n)];
                    }
                }

                huRoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, cnt + cnt2, false);
                takeRoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, -cnt - cnt2, false);
                takeRoomPlayer.getHuInfo().paixing[EHuType.POINT.ordinal()] = -cnt - cnt2;
                total += cnt + cnt2;
            }
            huRoomPlayer.getHuInfo().paixing[EHuType.POINT.ordinal()] = total;
        }
        if (0 != (shuKanModel & EShuKanType.HU.getValue())) {
            int cnt = 0;
            if (huCard < MahjongConstant.MJ_DONG) {
                cnt = ((huCard - 10) % 9) + 1;
            }
            int total = 0;
            if (huRoomPlayer.getHuInfo().isZiMo) {
                for (int i = 0; i < this.playerNum; ++i) {
                    MahjongPlayer player = (MahjongPlayer) this.allPlayer[i];
                    if (null == player || player.isGuest()) {
                        continue;
                    }
                    if (player.getUid() == huRoomPlayer.getUid()) {
                        continue;
                    }
                    player.addScore(Score.MJ_CALC_SHU_KAN_SCORE, -cnt, false);
                    huRoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, cnt, false);
                    player.getHuInfo().paixing[EHuType.POINT_HU.ordinal()] = -cnt;
                    total += cnt;
                }
            } else {
                huRoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, cnt, false);
                takeRoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, -cnt, false);
                takeRoomPlayer.getHuInfo().paixing[EHuType.POINT_HU.ordinal()] = -cnt;
                total = cnt;
            }
            huRoomPlayer.getHuInfo().paixing[EHuType.POINT_HU.ordinal()] = total;
        }
        if (0 != (shuKanModel & EShuKanType.ZDB.getValue())) {
            int cnt = huRoomPlayer.getHandCard()[MahjongConstant.MJ_ZHONG] + huRoomPlayer.getHandCard()[MahjongConstant.MJ_FA] + huRoomPlayer.getHandCard()[MahjongConstant.MJ_BAI];
            cnt += huRoomPlayer.getPgCardCnt()[MahjongConstant.MJ_ZHONG] + huRoomPlayer.getPgCardCnt()[MahjongConstant.MJ_FA] + huRoomPlayer.getPgCardCnt()[MahjongConstant.MJ_BAI];
            int total = 0;
            if (huRoomPlayer.getHuInfo().isZiMo) {
                for (int i = 0; i < this.playerNum; ++i) {
                    MahjongPlayer player = (MahjongPlayer) this.allPlayer[i];
                    if (null == player || player.isGuest()) {
                        continue;
                    }
                    if (player.getUid() == huRoomPlayer.getUid()) {
                        continue;
                    }
                    player.addScore(Score.MJ_CALC_SHU_KAN_SCORE, -cnt, false);
                    huRoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, cnt, false);
                    player.getHuInfo().paixing[EHuType.POINT_ZFB.ordinal()] = -cnt;
                    total += cnt;
                }
            } else {
                huRoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, cnt, false);
                takeRoomPlayer.addScore(Score.MJ_CALC_SHU_KAN_SCORE, -cnt, false);
                takeRoomPlayer.getHuInfo().paixing[EHuType.POINT_ZFB.ordinal()] = -cnt;
                total = cnt;
            }
            huRoomPlayer.getHuInfo().paixing[EHuType.POINT_ZFB.ordinal()] = total;
        }
        if (huRoomPlayer.getUid() != takeRoomPlayer.getUid()) {
            --huRoomPlayer.getHandCard()[huCard];
        }
        super.shuKan(takeRoomPlayer, huRoomPlayer, huCard);
    }

    @Override
    protected boolean isChaDaJiao() {
        // 查大叫
        return true;
    }

    @Override
    protected boolean isBanLiang() {
        // 半亮
        return true;
    }

    @Override
    protected boolean isBright(boolean bright) {
        return bright && this.allCard.size() >= 12;
    }

    @Override
    protected boolean isFenSanDui() {
        return true;
    }

    @Override
    protected boolean isBuyOneHorse() {
        return true;
    }

    @Override
    protected boolean isBuySixHorse() {
        return false;
    }

    @Override
    protected boolean isBuyOneGiveOneHorse() {
        return false;
    }
}
