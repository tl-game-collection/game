package com.xiuxiu.app.server.room.normal.poker.arch.locale;

import com.xiuxiu.app.server.room.normal.poker.arch.ArchRoom;
import com.xiuxiu.core.utils.ShuffleUtil;

import java.util.List;

public abstract class ArchLocale {
    private ArchRoom room;

    final int NUM_PACKS_OF_CARDS = 2; // 几副牌
    final int NUM_CARDS_PER_PACK = 54; // 每副牌的张数

    ArchLocale(ArchRoom room) {
        this.room = room;
    }

    public int features() {
        return 0;
    }

    /**
     * 重新洗牌
     */
    public void shuffle(List<Byte> cards) {
        for (byte i = 0; i < NUM_CARDS_PER_PACK; ++i) {
            for (int j = 0; j < NUM_PACKS_OF_CARDS; j++) {
                cards.add(i);
            }
        }

        ShuffleUtil.shuffle(cards);
    }

    /**
     * 地方特色相关结算
     */
    public abstract void basicScoreSettled(int contract);

    public ArchRoom getRoom() {
        return room;
    }

    public void setRoom(ArchRoom room) {
        this.room = room;
    }
}
