package com.xiuxiu.app.server.room.normal.poker.runFast;

/**
 *
 */
public enum ERunFastCardType {
    NORMAL(     0, "无"),
    FIG(        1, "无花果"),
    EightPair(  2, "8对"),
    PRIMULA(    3, "叫春");

    private String desc;
    private byte value;

    ERunFastCardType(int value, String desc) {
        this.value = (byte) value;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ERunFastCardType{" +
                "desc='" + desc + '\'' +
                ", value=" + value +
                '}';
    }
}
