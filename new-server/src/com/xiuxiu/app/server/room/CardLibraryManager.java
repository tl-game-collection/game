package com.xiuxiu.app.server.room;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardLibraryManager {
    private static class MajhongCardLibraryManagerHolder {
        private static CardLibraryManager instance = new CardLibraryManager();
    }
    public static CardLibraryManager I = MajhongCardLibraryManagerHolder.instance;

    private List<List<Byte>> allMahjongCard = new ArrayList<>();
    private int defaultUseIndex = 0;
    private List<List<Byte>> allPokerCard = new ArrayList<>();

    private CardLibraryManager() {
    }

    public void init() {
    }

    public void setMahjongCardLib(String cardValue) {
        if (StringUtil.isEmptyOrNull(cardValue)) {
            return;
        }
        allMahjongCard.clear();
        String[] all = cardValue.split("\n");
        for (int i = 0; i < all.length; ++i) {
            String[] line = all[i].split(",");

            List<Byte> card = new ArrayList<>();
            for (int j = 0, len2 = line.length; j < len2; ++j) {
                byte c = Byte.parseByte(line[j]);
                if (c >= MahjongUtil.MJ_1_WANG && c <= MahjongUtil.MJ_CARD_KINDS) {
                    card.add(c);
                }
            }
            allMahjongCard.add(card);
        }
    }

    public void setPokerCardLib(String cardValue) {
        if (StringUtil.isEmptyOrNull(cardValue)) {
            return;
        }
        allPokerCard.clear();
        String[] all = cardValue.split("\n");
        for (int i = 0; i < all.length; ++i) {
            String[] line = all[i].split(",");
            List<Byte> card = new ArrayList<>();
            for (int j = 0, len2 = line.length; j < len2; ++j) {
                byte c = Byte.parseByte(line[j]);
                if (c >= PokerUtil.THREE_BOX && c <= PokerUtil.KING) {
                    card.add(c);
                }
            }
            allPokerCard.add(card);
        }
    }

    public List<Byte> getMahjongCard() {
        return this.allMahjongCard.isEmpty() ? Collections.EMPTY_LIST : this.allMahjongCard.get(this.defaultUseIndex);
    }

    public List<Byte> getPokerCard() {
        return this.allPokerCard.isEmpty() ? Collections.EMPTY_LIST : this.allPokerCard.get(this.defaultUseIndex);
    }
}
