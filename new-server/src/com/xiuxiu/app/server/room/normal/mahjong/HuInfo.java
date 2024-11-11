package com.xiuxiu.app.server.room.normal.mahjong;

public class HuInfo {
    public boolean isZiMo;                  // 自摸
    public boolean isHu;                    // 屁胡
    public boolean isPengPengHu;            // 碰碰胡
    public boolean isKWX;                   // 卡五星
    public boolean isQiDui;                 // 七对
    public boolean isXiaoSanYuanQiDui;      // 小三阳七对
    public boolean isXiaoSanYuan;           // 小三元
    public boolean isGangShangHu;           // 杠上胡
    public boolean isGangShangPao;          // 杠上炮
    public boolean isQiangGangHu;           // 抢杠胡
    public boolean isWillTwoFiveEight;      // 258将
    public boolean isYouZhongYou;           // 油中油
    public byte youZhongYouFumbleCard = -1; // 油中油摸牌;

    public int[] paixing = new int[EHuType.MAX.ordinal()];

    public String getPaiXing() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = paixing.length; i < len; ++i) {
            if (0 != paixing[i]) {
                sb.append(EHuType.values()[i] + " X" + paixing[i] + ", ");
            }
        }
        return sb.toString();
    }

    public void clear() {
        for (int i = 0, len = EHuType.MAX.ordinal(); i < len; ++i) {
            this.paixing[i] = 0;
        }

        this.isZiMo = false;
        this.isHu = false;
        this.isPengPengHu = false;
        this.isKWX = false;
        this.isXiaoSanYuan = false;
        this.isQiDui = false;
        this.isXiaoSanYuanQiDui = false;
        this.isGangShangHu = false;
        this.isGangShangPao = false;
        this.isQiangGangHu = false;
        this.isWillTwoFiveEight = false;
        this.isYouZhongYou = false;
        this.youZhongYouFumbleCard = -1;
    }
}
