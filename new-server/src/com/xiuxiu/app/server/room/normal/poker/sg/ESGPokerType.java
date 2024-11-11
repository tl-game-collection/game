package com.xiuxiu.app.server.room.normal.poker.sg;

public enum ESGPokerType {
    NONE("空", 0,1,1),
    SG_0DIAN("0点",1,1,1),
    SG_1DIAN("1点",2,1,1),
    SG_2DIAN("2点",3,1,1),
    SG_3DIAN("3点",4,1,1),
    SG_4DIAN("4点",5,1,1),
    SG_5DIAN("5点",6,1,1),
    SG_6DIAN("6点",7,1,1),
    SG_7DIAN("7点",8,2,2),
    SG_8DIAN("8点",9,3,3),
    SG_9DIAN("9点",10,4,4),
    SG_SG("三公",11,5,5),
    SG_MINSG("小三公",12,5,6),
    SG_MAXSG("大三公",13,5,7),
    SG_BAOJIU("暴玖",14,5,8),
    ;

    private String desc;
    private byte value;
    private int mulType1;
    private int mulType2;

    ESGPokerType(String desc, int value) {
        this.desc = desc;
        this.value = (byte) value;
    }

    ESGPokerType(String desc, int value, int mulType1,int mulType2) {
        this.desc = desc;
        this.value = (byte) value;
        this.mulType1 = mulType1;
        this.mulType2 = mulType2;
    }

    public String getDesc() {
        return this.desc;
    }

    public byte getValue() {
        return this.value;
    }

    public int getMul(int mulType) {
        if (mulType == 1){
            return mulType1;
        }else if (mulType == 2){
            return mulType2;
        }
        return 1;
    }

    public static ESGPokerType parse(byte value) {
        switch (value) {
            case 1:
                return SG_0DIAN;
            case 2:
                return SG_1DIAN;
            case 3:
                return SG_2DIAN;
            case 4:
                return SG_3DIAN;
            case 5:
                return SG_4DIAN;
            case 6:
                return SG_5DIAN;
            case 7:
                return SG_6DIAN;
            case 8:
                return SG_7DIAN;
            case 9:
                return SG_8DIAN;
            case 10:
                return SG_9DIAN;
            case 11:
                return SG_SG;
            case 12:
                return SG_MINSG;
            case 13:
                return SG_MAXSG;
            case 14:
                return SG_BAOJIU;
        }
        return NONE;
    }
}
