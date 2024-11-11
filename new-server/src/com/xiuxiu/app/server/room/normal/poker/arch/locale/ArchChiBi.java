package com.xiuxiu.app.server.room.normal.poker.arch.locale;

import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfArchGameOverInfo;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.poker.arch.ArchRoom;
import com.xiuxiu.app.server.room.normal.poker.arch.ArchRule;
import com.xiuxiu.app.server.room.player.poker.ArchPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 赤壁打滚
 */
public class ArchChiBi extends ArchLocale {
    // 飘分
    private int rulePiao;
    private static final int RULE_PIAO_0 = 0;
    private static final int RULE_PIAO_1 = 1;
    private static final int RULE_PIAO_2 = 2;

    // 花牌
    private int ruleHua;
    private static final int RULE_HUA_0 = 0;
    private static final int RULE_HUA_1 = 1;
    private static final int RULE_HUA_2 = 2;

    // 得分点
    private static final int SP_HUA = 1; // 花牌
    private static final int SP_JOKER_BOMB = 2; // 天炸
    private static final int SP_8_XI = 3; // 8喜
    private static final int SP_7_XI = 4; // 7喜
    private static final int SP_4_WSK = 5; // 4五十K

    public ArchChiBi(ArchRoom room) {
        super(room);

        this.rulePiao = room.getRule().getOrDefault(RoomRule.RR_ARCH_PIAO, RULE_PIAO_0);
        this.ruleHua = room.getRule().getOrDefault(RoomRule.RR_ARCH_HUA, RULE_HUA_0);
    }

    @Override
    public int features() {
        return ArchRule.FEATURE_HUA_PAI | ArchRule.FEATURE_COMBO_BOMB | ArchRule.FEATURE_JOKER_BOMB;
    }

    @Override
    public void shuffle(List<Byte> cards) {
        for (int i = 0; i < this.ruleHua; i++) {
            cards.add(ArchRule.HUA_PAI);
        }
        super.shuffle(cards);
    }

    private void addScorePoint(ArchPlayer player, int key, int value) {
        this.addScorePoint(player.getUid(), key, value);
    }

    private void addScorePoint(long playerUid, int key, int value) {
        PCLIPokerNtfArchGameOverInfo result = this.getRoom().getBureauResult();
        PCLIPokerNtfArchGameOverInfo.GameOverInfo playerInfo = result.allGameOverInfo.get(playerUid);
        PCLIPokerNtfArchGameOverInfo.ScorePoint scorePoint = null;
        for (PCLIPokerNtfArchGameOverInfo.ScorePoint sp : playerInfo.scorePoints) {
            if (sp.key == key) {
                scorePoint = sp;
                break;
            }
        }
        int score = this.getRoom().getScoreWithBase(value);
        if (scorePoint == null) {
            scorePoint = new PCLIPokerNtfArchGameOverInfo.ScorePoint(key, score);
            playerInfo.scorePoints.add(scorePoint);
        } else {
            scorePoint.value += score;
        }
    }

    @Override
    public void basicScoreSettled(int contract) {
        ArchRoom room = this.getRoom();

        // 飘
        if (this.rulePiao != RULE_PIAO_0) {
            int piao = this.rulePiao == RULE_PIAO_1 ? 1 : 2;
            ArchPlayer banker = (ArchPlayer) room.getRoomPlayer(room.getBankerIndex());
            for (int i = 0; i < room.getMaxPlayerCnt(); i++) {
                IRoomPlayer player = room.getRoomPlayer(i);
                if (player != null && !player.isGuest()) {
                    boolean playerWin = room.getPlayerScore(player) > 0;
                    if (contract == ArchRule.CONTRACT_1V3) {
                        int playerScore = player == banker ? piao * 3 : piao;
                        room.addPlayerScore(player, playerWin ? playerScore : -playerScore);
                    } else {
                        room.addPlayerScore(player, playerWin ? piao : -piao);
                    }
                }
            }
        }

        // 花牌
        if (this.ruleHua == RULE_HUA_1) {
            if (contract == ArchRule.CONTRACT_1V3) {
                ArchPlayer banker = (ArchPlayer) room.getRoomPlayer(room.getBankerIndex());
                boolean bankerWin = room.getPlayerScore(banker) > 0;
                boolean bankerHua = banker.getDealtCards().contains(ArchRule.HUA_PAI);

                for (int i = 0; i < room.getMaxPlayerCnt(); i++) {
                    ArchPlayer player = (ArchPlayer) room.getRoomPlayer(i);
                    if (player != null && !player.isGuest()) {
                        boolean playerIsBanker = player.getIndex() == room.getBankerIndex();
                        if (bankerHua) { // 庄家花
                            int baseScore = bankerWin ? 2 : -2;
                            room.addPlayerScore(player, playerIsBanker ? baseScore * 3 : -baseScore);
                            this.addScorePoint(player, SP_HUA, playerIsBanker ? baseScore * 3 : -baseScore);
                        } else { // 闲家花
                            if (playerIsBanker) {
                                room.addPlayerScore(player, bankerWin ? 2 : -2);
                                this.addScorePoint(player, SP_HUA, bankerWin ? 2 : -2);
                            } else if (player.getDealtCards().contains(ArchRule.HUA_PAI)) {
                                room.addPlayerScore(player, bankerWin ? -2 : 2);
                                this.addScorePoint(player, SP_HUA, bankerWin ? -2 : 2);
                            }
                        }
                    }
                }
            } else {
                boolean winnerHua = false; // 赢家花
                for (int i = 0; i < room.getMaxPlayerCnt(); i++) {
                    ArchPlayer player = (ArchPlayer) room.getRoomPlayer(i);
                    if (player != null && !player.isGuest() && player.getDealtCards().contains(ArchRule.HUA_PAI)) {
                        winnerHua = room.getPlayerScore(player) > 0;
                        break;
                    }
                }

                for (int i = 0; i < room.getMaxPlayerCnt(); i++) {
                    ArchPlayer player = (ArchPlayer) room.getRoomPlayer(i);
                    if (player != null && !player.isGuest()) {
                        if (player.getDealtCards().contains(ArchRule.HUA_PAI)) {
                            room.addPlayerScore(player, winnerHua ? 2 : -2);
                            this.addScorePoint(player, SP_HUA, winnerHua ? 2 : -2);
                        } else if (winnerHua) {
                            if (room.getPlayerScore(player) < 0) {
                                room.addPlayerScore(player, -1);
                                this.addScorePoint(player, SP_HUA, -1);
                            }
                        } else {
                            if (room.getPlayerScore(player) > 0) {
                                room.addPlayerScore(player, 1);
                                this.addScorePoint(player, SP_HUA, 1);
                            }
                        }
                    }
                }
            }
        } else if (this.ruleHua == RULE_HUA_2) {
            if (contract == ArchRule.CONTRACT_1V3) {
                ArchPlayer banker = (ArchPlayer) room.getRoomPlayer(room.getBankerIndex());
                boolean bankerWin = room.getPlayerScore(banker) > 0; // 庄家赢
                int bankerHua = 0; // 庄家花牌数量
                for (byte card : banker.getDealtCards()) {
                    if (card == ArchRule.HUA_PAI) {
                        bankerHua++;
                    }
                }

                for (int i = 0; i < room.getMaxPlayerCnt(); i++) {
                    ArchPlayer player = (ArchPlayer) room.getRoomPlayer(i);
                    if (player != null && !player.isGuest()) {
                        boolean playerIsBanker = player.getIndex() == room.getBankerIndex();
                        if (bankerHua == 2) { // 庄家2花
                            int baseScore = bankerWin ? 4 : -4;
                            room.addPlayerScore(player, playerIsBanker ? baseScore * 3 : -baseScore);
                            this.addScorePoint(player, SP_HUA, playerIsBanker ? baseScore * 3 : -baseScore);
                        } else if (bankerHua == 1) { // 庄家1花
                            if (playerIsBanker) {
                                room.addPlayerScore(player, bankerWin ? 8 : -8);
                                this.addScorePoint(player, SP_HUA, bankerWin ? 8 : -8);
                            } else if (bankerWin) {
                                boolean playerHua = player.getDealtCards().contains(ArchRule.HUA_PAI);
                                room.addPlayerScore(player, playerHua ? -4 : -2);
                                this.addScorePoint(player, SP_HUA, playerHua ? -4 : -2);
                            } else {
                                boolean playerHua = player.getDealtCards().contains(ArchRule.HUA_PAI);
                                room.addPlayerScore(player, playerHua ? 4 : 2);
                                this.addScorePoint(player, SP_HUA, playerHua ? 4 : 2);
                            }
                        } else { // 闲家2花
                            if (playerIsBanker) {
                                room.addPlayerScore(player, bankerWin ? 4 : -4);
                                this.addScorePoint(player, SP_HUA, bankerWin ? 4 : -4);
                            } else {
                                for (byte card : player.getDealtCards()) {
                                    if (card == ArchRule.HUA_PAI) {
                                        room.addPlayerScore(player, bankerWin ? -2 : 2);
                                        this.addScorePoint(player, SP_HUA, bankerWin ? -2 : 2);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                int winnerHua = 0; // 赢方花牌数量
                for (int i = 0; i < room.getMaxPlayerCnt(); i++) {
                    ArchPlayer player = (ArchPlayer) room.getRoomPlayer(i);
                    if (player != null && !player.isGuest()
                            && room.getPlayerScore(player) > 0 && player.getDealtCards().contains(ArchRule.HUA_PAI)) {
                        for (Byte b : player.getDealtCards()) {
                            if (b == ArchRule.HUA_PAI) {
                                winnerHua++;
                            }
                        }
                    }
                }

                if (winnerHua == 1) { // 输家1花，赢家1花\
                    for (int i = 0; i < room.getMaxPlayerCnt(); i++) {
                        ArchPlayer player = (ArchPlayer) room.getRoomPlayer(i);
                        if (player != null && !player.isGuest()) {
                            int extraScore = player.getDealtCards().contains(ArchRule.HUA_PAI) ? 3 : 1;
                            boolean playerWin = room.getPlayerScore(player) > 0;
                            room.addPlayerScore(player, playerWin ? extraScore : -extraScore);
                            this.addScorePoint(player, SP_HUA, playerWin ? extraScore : -extraScore);
                        }
                    }
                } else { // 2花在同一边
                    for (int i = 0; i < room.getMaxPlayerCnt(); i++) {
                        ArchPlayer player = (ArchPlayer) room.getRoomPlayer(i);
                        if (player != null && !player.isGuest()) {
                            boolean playerWin = room.getPlayerScore(player) > 0;
                            if (player.getDealtCards().contains(ArchRule.HUA_PAI)) {
                                for (byte card : player.getDealtCards()) {
                                    if (card == ArchRule.HUA_PAI) {
                                        room.addPlayerScore(player, playerWin ? 2 : -2);
                                        this.addScorePoint(player, SP_HUA, playerWin ? 2 : -2);
                                    }
                                }
                            } else if (winnerHua == 2) { // 赢家2花
                                if (!playerWin) {
                                    room.addPlayerScore(player, -2);
                                    this.addScorePoint(player, SP_HUA, -2);
                                }
                            } else { // 输家2花
                                if (playerWin) {
                                    room.addPlayerScore(player, 2);
                                    this.addScorePoint(player, SP_HUA, 2);
                                }
                            }
                        }
                    }
                }
            }
        }

        // 天炸，7喜，8喜等
        Map<ArchPlayer, Map<Byte, Integer>> playerCardCount = new HashMap<>();
        for (int i = 0; i < room.getMaxPlayerCnt(); i++) {
            ArchPlayer player = (ArchPlayer) room.getRoomPlayer(i);
            if (player != null && !player.isGuest()) {
                Map<Byte, Integer> cardCount = new HashMap<>();
                playerCardCount.put(player, cardCount);
                for (byte card : player.getDealtCards()) {
                    byte value = ArchRule.getCardValue(card == PokerUtil.KINGLET ? PokerUtil.KING : card);
                    int count = cardCount.getOrDefault(value, 0);
                    cardCount.put(value, count + 1);
                }
            }
        }

        for (Map.Entry<ArchPlayer, Map<Byte, Integer>> playerEntry : playerCardCount.entrySet()) {
            Map<Byte, Integer> cardCount = playerEntry.getValue();
            for (Map.Entry<Byte, Integer> cardEntry : cardCount.entrySet()) {
                if (cardEntry.getKey() == PokerUtil.getCardValue(PokerUtil.KING)) {
                    if (cardEntry.getValue() == 4) { // 有天炸
                        this.playerWinOthers(playerEntry.getKey(), 1, SP_JOKER_BOMB);
                    }
                } else {
                    if (cardEntry.getValue() == 7) { // 有7喜
                        this.playerWinOthers(playerEntry.getKey(), 1, SP_7_XI);
                    } else if (cardEntry.getValue() == 8) { // 有8喜
                        this.playerWinOthers(playerEntry.getKey(), 2, SP_8_XI);
                    }
                }
            }

            // 4 五十K
            if (cardCount.getOrDefault(PokerUtil._5, 0) >= 4
                    && cardCount.getOrDefault(PokerUtil._10, 0) >= 4
                    && cardCount.getOrDefault(PokerUtil._K, 0) >= 4) {
                this.playerWinOthers(playerEntry.getKey(), 1, SP_4_WSK);
            }
        }
    }

    private void playerWinOthers(ArchPlayer player, int score, int scorePoint) {
        ArchRoom room = this.getRoom();
        for (int i = 0; i < room.getMaxPlayerCnt(); i++) {
            ArchPlayer loser = (ArchPlayer) room.getRoomPlayer(i);
            if (loser != null && !loser.isGuest() && loser.getUid() != player.getUid()) {
                room.addPlayerScore(loser, -score);
                this.addScorePoint(loser, scorePoint, -score);

                room.addPlayerScore(player, score);
                this.addScorePoint(player, scorePoint, score);
            }
        }
    }
}
