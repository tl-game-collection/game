package com.xiuxiu.app.server.room.normal.mahjong2;

public enum EPaiXing {
    NONE(0, 0),
    
    /**红中麻将**/
    //332屁胡
    HZMJ_NORMAL(10005001, 0, 1),
    //碰碰胡
    HZMJ_PPH(10005002, 1, 1),
    //七对
    HZMJ_QD(10005003, 2, 1),
    //天胡
    HZMJ_TH(10005004, 3, 1),
    //清一色
    HZMJ_QYS(10005005, 4, 1),

    YCXL_NORMAL(10011001, 0, 1),
    YCXL_MEN_QING(10011002, 1, 2),
    YCXL_DUIDUI_HU(10011003, 2, 4),
    YCXL_QING_YI_SE(10011004, 3, 6),
    YCXL_QING_DUI(10011005, 4, 10),
    YCXL_QING_DUI_MEN_QING(10011006, 4, 11),
    YCXL_QING_YI_SE_MEN_QING(10011007, 4, 7),
    YCXL_DUIDUI_HU_MEN_QING(10011008, 4, 5),

    /**武汉麻将**/
    //332屁胡
    WHMJ_NORMAL(10012001, 0, 1),
    //清一色
    WHMJ_QING_YI_SE(10012002, 1, 1),
    //将一色
    WHMJ_JIANG_YI_SE(10012003, 2, 1),
    //风一色
    WHMJ_FENG_YIS_SE(10012004, 3, 1),
    //碰碰胡
    WHMJ_PENG_PENG_HU(10012005, 4, 1),
    //全求人
    WHMJ_QUAN_QIU_REN(10012006, 5, 1),
    //杠开
    WHMJ_GANG_KAI(10012007, 6, 1),
    //抢杠胡
    WHMJ_QIANG_GANG_HU(10012008, 7, 1),
    //海底捞
    WHMJ_HAI_DI_LAO(10012009, 8, 1),
    //硬胡（PS:无癞子）
    WHMJ_YING(10012010, 9, 1),
    //七对
    WHMJ_QI_DUI(10012011, 10, 1),
    //门清
    WHMJ_MEN_QING(10012012, 11, 1),
    //杠上炮
    WHMJ_GANG_SHANG_PAO(10012013, 12, 1),
    //豪华
    WHMJ_HAO_HUA(10012014, 13, 1),
    //双豪华
    WHMJ_SHUANG_HAO_HUA(10012015, 14, 1),
    //超豪华
    WHMJ_CHAO_HAO_HUA(10012016, 15, 1),

    CBDD_NORMAL(10014001, 0, 2),

    XZDD_NORMAL(10006001, 0, 0),
    XZDD_PPH(10006002, 1, 1),
    XZDD_QYS(10006003, 2, 2),
    XZDD_QD(10006004, 3, 2),
    XZDD_XUAN_JIU(10006005, 4, 2),
    XZDD_QING_DUI(10006006, 5, 3),
    XZDD_JIANG_DUI(10006007, 6, 3),
    XZDD_LQD(10006008, 7, 4),
    XZDD_QQD(10006009, 8, 4),
    XZDD_QXJ(10006010, 9, 4),
    XZDD_MQ(10006011, 10, 1),
    XZDD_ZZ(10006012, 11, 1),
    XZDD_QGH(10006013, 12, 1),
    XZDD_GK(10006014, 13, 1),
    XZDD_TH(10006015, 14, 3),
    XZDD_DH(10006016, 15, 2),
    XZDD_GEN(10006017, 16, 1),
    XZDD_GSP(10006018, 17, 1),

    ZZMJ_NORMAL(10016001, 0, 0),

    /**长沙麻将**/
    //332屁胡
    CSMJ_NORMAL(10017001, 0, 1),
    //清一色
    CSMJ_QYS(10017002, 1, 6),
    CSMJ_LLS(10017003, 2, 6),
    CSMJ_DSX(10017004, 3, 6),
    CSMJ_BBH(10017005, 4, 6),
    CSMJ_YZH(10017006, 5, 6),
    //节节高
    CSMJ_JJG(10017007, 6, 6),
    CSMJ_ST(10017008, 7, 6),
    CSMJ_JTYN(10017009, 8, 6),
    //碰碰胡
    CSMJ_PPH(10017010, 9, 6),
    //七小对
    CSMJ_QXD(10017011, 10, 6),
    //豪华七小对
    CSMJ_HHQXD(10017012, 11, 12),
    //全求人
    CSMJ_QQR(10017013, 12, 6),
    CSMJ_JJH(10017014, 13, 6),
    CSMJ_HDH(10017015, 14, 6),
    CSMJ_HDP(10017016, 15, 6),
    //门清
    CSMJ_MQ(10017017, 16, 6),
    //天胡
    CSMJ_TH(10017018, 17, 6),
    //地胡
    CSMJ_DH(10017019, 18, 6),
    //清一色2（混一色？？）
    CSMJ_QYS2(10017020, 19, 6),
    //杠开
    CSMJ_GK(10017021, 20, 6),
    //杠上炮
    CSMJ_GSP(10017022, 21, 6),
    //抢杠胡
    CSMJ_QGH(10017023, 22, 6),

    // 麻城麻将
    MCMJ_NORMAL(10019001, 0, 1), // 屁胡
    MCMJ_MEN_QING(10019002, 1, 1), // 门前清
    MCMJ_PENG_PENG_HU(10019003, 2, 1), // 碰碰胡
    MCMJ_QING_YI_SE(10019004, 3, 1), // 清一色
    MCMJ_FENG_YI_SE(10019005, 4, 1), // 风一色
    MCMJ_JIANG_YI_SE(10019006, 5, 1), // 将一色
    MCMJ_QI_DUI(10019007, 6, 1), // 七对
    MCMJ_GANG_KAI(10019008, 7, 1), // 杠上开花
    MCMJ_YING(10019009, 8, 1), // 硬胡
    MCMJ_HAO_HUA(10019010, 9, 1), // 豪华

    // 仙桃麻将
    XTMJ_NORMAL(10020001, 0, 1), // 屁胡
    XTMJ_YING(10020002, 1, 1), // 硬胡
    XTMJ_GANG_KAI(10020003, 2, 1), // 杠上开花
    XTMJ_QIANG_GANG_HU(10020004, 3, 1), // 抢杠胡
    XTMJ_GANG_SHANG_PAO(10020005, 4, 1), // 杠上炮

    // 大冶麻将
    DYMJ_NORMAL(10027001, 0, 1),        // 屁胡
    DYMJ_QING_YI_SE(10027002, 1, 1),    // 清一色
    DYMJ_JIANG_YI_SE(10027003, 2, 1),   // 将一色
    DYMJ_FENG_YIS_SE(10027004, 3, 1),   // 风一色
    DYMJ_QUAN_QIU_REN(10027006, 5, 1),  // 全球人
    DYMJ_PENG_PENG_HU(10027005, 4, 1),  // 碰碰胡
    DYMJ_QIANG_GANG_HU(10027008, 7, 1), // 抢杠胡
    DYMJ_GANG_KAI(10027007, 6, 1),      // 杠开
    DYMJ_HAI_DI_LAO(10027009, 8, 1),    // 海底捞
    DYMJ_YING(10027010, 9, 1),          // 硬胡、

    // 阳新麻将
    YXMJ_NORMAL(10023001, 0, 1), // 屁胡
    YXMJ_MEN_QING(10023002, 1, 1), // 门前清
    YXMJ_PENG_PENG_HU(10023003, 2, 1), // 碰碰胡
    YXMJ_QING_YI_SE(10023004, 3, 1), // 清一色
    YXMJ_JIANG_YI_SE(10023005, 4, 1), // 将一色
    YXMJ_QI_DUI(10023006, 5, 1), // 七对
    YXMJ_HAO_HUA(10023007, 6, 1), // 豪华七对
    YXMJ_YING(10023008, 7, 1), // 硬胡
    YXMJ_GANG_KAI(10023009, 8, 1), // 杠上开花
    YXMJ_QIANG_GANG_HU(10023010, 9, 1), // 抢杠胡
    YXMJ_HAI_DI_LAO(10023011, 10, 1), // 海底捞月
    YXMJ_GANG_SHANG_PAO(10023012, 11, 1), // 杠上炮

    // 黄石麻将
    HSMJ_NORMAL(10024001, 0, 1), // 屁胡
    HSMJ_MEN_QING(10024002, 1, 1), // 门前清
    HSMJ_PENG_PENG_HU(10024003, 2, 1), // 碰碰胡
    HSMJ_QING_YI_SE(10024004, 3, 1), // 清一色
    HSMJ_JIANG_YI_SE(10024005, 4, 1), // 将一色
    HSMJ_QI_DUI(10024006, 5, 1), // 七对
    HSMJ_HAO_HUA(10024007, 6, 1), // 豪华
    HSMJ_YING(10024008, 7, 1), // 硬胡
    HSMJ_HAO_HUA_2(10024009, 8, 1), // 双豪华

    // 阳新麻将
    YYMJ_NORMAL(10026001, 0, 1), // 屁胡
    YYMJ_TIAN_HU(10026002, 1, 1), // 天胡
    YYMJ_PENG_PENG_HU(10026003, 2, 1), // 碰碰胡
    YYMJ_QI_DUI(10026004, 3, 1), // 七对
    YYMJ_HAO_HUA(10026005, 4, 1), // 豪华七对
    YYMJ_HAO_HUA_2(10026006, 5, 1), // 双豪华七对
    YYMJ_QUAN_QIU_REN(10026007, 6, 1), // 双豪华
    YYMJ_QING_YI_SE(10026008, 7, 1), // 清一色
    YYMJ_JIANG_YI_SE(10026009, 8, 1), // 将一色
    YYMJ_YI_TIAO_LONG(10026010, 9, 1), // 一条龙
    YYMJ_HAI_DI_LAO(10026011, 10, 1), // 海底捞月
    YYMJ_MEN_QING(10026012, 11, 1), // 门前清
    YYMJ_GANG_KAI(10026013, 12, 1), // 杠上开花
    YYMJ_QIANG_GANG_HU(10026014, 13, 1), // 抢杠胡
    YYMJ_BAO_TING_HU(10026015, 14, 1), // 报听胡

    //福州麻将
    FZMJ_NORMAL(10025001, 0, 1), // 屁胡
    FZMJ_TIAN_HU(10025002, 1, 1), // 天胡
    FZMJ_QIANG_JIN(10025003, 2, 1), // 抢金
    FZMJ_WU_HUA_WU_GANG(10025004, 3, 1), // 无花无杠
    FZMJ_YI_ZHANG_HUA(10025005, 4, 1), // 一张花
    FZMJ_SAN_JIN_DAO(10025006, 5, 1), // 三金倒
    FZMJ_JIN_QUE(10025007, 6, 1), // 金雀
    FZMJ_JIN_LONG(10025008, 7, 1), // 金龙
    FZMJ_HUN_YI_SE(10025009, 8, 1), // 混一色
    FZMJ_QING_YI_SE(10025010, 9, 1), // 清一色

    // 扣点麻将
    KDMJ_NORMAL(10021001, 0, 1), // 屁胡
    KDMJ_QING_YI_SE(10021002, 1, 1), // 清一色
    KDMJ_QI_DUI(10021003, 2, 1), // 七对
    KDMJ_HAO_HUA(10021004, 3, 1), // 豪华七对
    KDMJ_YI_TIAO_LONG(10021005, 4, 1),  // 一条龙
    KDMJ_QIANG_GANG_HU(10021006, 5, 1),// 抢杠胡
    KDMJ_FENG_YI_SE(10021007, 6, 1), // 风一色
    KDMJ_JIAN_ZI_HU(10021008, 7, 1), //见字胡
    
    ;

    private int clientValue;
    private int index;
    private int defaultValue;

    EPaiXing(int clientValue, int index) {
        this(clientValue, index, 0);
    }

    EPaiXing(int clientValue, int index, int defaultValue) {
        this.clientValue = clientValue;
        this.index = index;
        this.defaultValue = defaultValue;
    }

    public int getClientValue() {
        return clientValue;
    }

    public int getIndex() {
        return index;
    }

    public int getDefaultValue() {
        return defaultValue;
    }
}
