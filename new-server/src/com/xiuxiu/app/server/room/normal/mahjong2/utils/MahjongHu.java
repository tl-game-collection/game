package com.xiuxiu.app.server.room.normal.mahjong2.utils;

// 麻将胡牌算番
// 注意：按照国标算番的话，会超过64种算番胡牌
public interface MahjongHu {
    long PI_HU               = 0x00000001L; // 屁胡

    long ZI_MO               = 0x00001000L; // 自摸
    long MING_GANG           = 0x00002000L; // 明杠
    long AN_GANG             = 0x00004000L; // 暗杠
    long QUAN_QIU_REN        = 0x00008000L; // 全求人
    long QIANG_GANG_HU       = 0x00010000L; // 抢杠和
    long QI_DUI              = 0x00020000L; // 七对
    long PENG_PENG_HU        = 0x00040000L; // 碰碰胡
    long QING_YI_SE          = 0x00080000L; // 清一色
    long FENG_YI_SE          = 0x00100000L; // 风一色
    long JIANG_YI_SE         = 0x00200000L; // 将一色
    long MEN_QIAN_QING       = 0x00400000L; // 门前清
    long SHI_SAN_YAO         = 0x00800000L; // 十三幺
    long DA_SAN_YUAN         = 0x01000000L; // 大三元
    long DA_SI_XI            = 0x02000000L; // 大四喜
    long XIAO_SAN_YUAN       = 0x04000000L; // 小三元
    long XIAO_SI_XI          = 0x08000000L; // 小四喜
    long GANG_SHANG_KAI_HUA  = 0x10000000L; // 杠上开花
    long HAI_DI_LAO          = 0x20000000L; // 海底捞月
    long GANG_SHANG_PAO      = 0x40000000L; // 杠上炮
    long QING_LONG           = 0x80000000L; // 清龙（一条龙）
}
