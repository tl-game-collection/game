package com.xiuxiu.app.server.room.normal.mahjong2;

public class CPGNode {
    public enum EType {
        // 0: 碰, 1: 放杠, 2: 明杠 3: 暗杠, 4: 右(后)吃, 5: 左(前)吃, 6: 中吃, 7: 任意3张, 8: 癞子杠, 9: 皮子杠
        BUMP("碰"),
        BAR_FANG("放杠"),
        BAR_MING("明杠"),
        BAR_AN("暗杠"),
        EAT_RIGHT("右吃"),
        EAT_LEFT("左吃"),
        EAT_MIDDLE("中吃"),
        ANY_THREE("任意3张"),
        BAR_LAIZI("癞子杠"),
        BAR_PI("皮子杠"),
        ;

        private String desc;

        EType(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public boolean isEat() {
            return CPGNode.EType.EAT_MIDDLE == this || CPGNode.EType.EAT_RIGHT == this || CPGNode.EType.EAT_LEFT == this;
        }

        public boolean isBar() {
            return EType.BAR_AN == this || EType.BAR_PI == this || EType.BAR_MING == this || EType.BAR_FANG == this || EType.BAR_LAIZI == this;
        }

        public boolean isBump() {
            return EType.BUMP == this;
        }
    }

    protected EType type;
    protected int takePlayerIndex;
    protected byte card1;
    protected byte card2;
    protected byte card3;
    protected byte card4;
    protected boolean ting;

    public CPGNode(int takePlayerIndex, EType type, byte cardValue) {
        this.type = type;
        this.takePlayerIndex = takePlayerIndex;
        this.card1 = cardValue;
    }

    public CPGNode(int takePlayerIndex, byte card1, byte card2, byte card3) {
        this.type = EType.ANY_THREE;
        this.takePlayerIndex = takePlayerIndex;
        this.card1 = card1;
        this.card2 = card2;
        this.card3 = card3;
    }

    public boolean isTing() {
        return ting;
    }

    public void setTing(boolean ting) {
        this.ting = ting;
    }

    public EType getType() {
        return type;
    }

    public void setType(EType type) {
        this.type = type;
    }

    public byte getCard1() {
        return card1;
    }

    public byte getCard2() {
        return card2;
    }

    public byte getCard3() {
        return card3;
    }

    public byte getCard4() {
        return card4;
    }

    public int getTakePlayerIndex() {
        return takePlayerIndex;
    }

    public boolean isEat() {
        return this.type.isEat();
    }

    public boolean isBar() {
        return this.type.isBar();
    }

    public boolean isBump() {
        return this.type.isBump();
    }
}
