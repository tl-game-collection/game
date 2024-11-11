package com.xiuxiu.app.server.room.normal.poker.arch;

import com.xiuxiu.algorithm.poker.EPokerCardType;

public enum ArchCardTypeEnum {
    NONE(EPokerCardType.NONE),
    SINGLE(EPokerCardType.SINGLE),
    DOUBLE(EPokerCardType.DOUBLE),
    THREE(EPokerCardType.THREE),
    DOUBLE_LINE(EPokerCardType.DOUBLE_LINE),
    THREE_LINE(EPokerCardType.THREE_LINE),
    WUSHIK_WSKBOMB(EPokerCardType.WUSHIK_WSKBOMB),
    WUSHIK_WSKBIGBOMB(EPokerCardType.WUSHIK_WSKBIGBOMB),
    BOMB(EPokerCardType.BOMB),
    JOKER_BOMB("天炸", 100),
    COMBO_BOMB("连炸", 101);

    private String desc;
    private byte value;

    ArchCardTypeEnum(EPokerCardType type) {
        this.desc = type.getDesc();
        this.value = type.getValue();
    }

    ArchCardTypeEnum(String desc, int value) {
        this.desc = desc;
        this.value = (byte) value;
    }

    public String getDesc() {
        return desc;
    }

    public byte getValue() {
        return value;
    }

    public EPokerCardType toPokerCardType() {
        if (this == JOKER_BOMB || this == COMBO_BOMB) {
            return EPokerCardType.BOMB;
        }
        return EPokerCardType.parse(this.getValue());
    }
}
