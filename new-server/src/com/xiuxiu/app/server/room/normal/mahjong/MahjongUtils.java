package com.xiuxiu.app.server.room.normal.mahjong;

import com.xiuxiu.app.server.table.TbKWXFangManager;

import java.util.List;

public final class MahjongUtils {
    private static ThreadLocal<byte[]> tempCards = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[MahjongConstant.MJ_CARD_KINDS];
        }
    };
    private static ThreadLocal<byte[]> temp3Cards = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[MahjongConstant.MJ_CARD_KINDS];
        }
    };
    private static ThreadLocal<byte[]> temp4Cards = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[MahjongConstant.MJ_CARD_KINDS];
        }
    };
    private static ThreadLocal<byte[]> temp5Cards = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[MahjongConstant.MJ_CARD_KINDS];
        }
    };

    public static int isHuWithKWX(byte[] card, byte[] cpgCard, byte cardValue, boolean liang, boolean isFullChannel, HuInfo huInfo, boolean addHuCard, boolean fenSanDui, int city) {
        // 胡
        int curCardCnt = 0;
        int cpgCnt = 0;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            curCardCnt += card[i];
            tempCards.get()[i] = card[i];
            if (cpgCard[i] > 0) {
                ++cpgCnt;
            }
            //temp2Cards[i] = tempCards.get()[i];
            temp3Cards.get()[i] = card[i];
            temp4Cards.get()[i] = card[i];
            temp4Cards.get()[i] += 0 != cpgCard[i] ? 3 : 0;
            temp5Cards.get()[i] = (byte) (0 != cpgCard[i] ? 3 : 0);
        }
        if (addHuCard) {
            ++tempCards.get()[cardValue];
            //++temp2Cards[cardValue];
            ++temp3Cards.get()[cardValue];
            ++temp4Cards.get()[cardValue];
            ++curCardCnt;
        }
        huInfo.clear();
        boolean isQiDui = isQiDui(tempCards.get());
        byte will = isHu(tempCards.get(), cpgCnt);
        boolean isHu = isQiDui ? true : -1 != will;
        if (!isHu) {
            return 0;
        }
        huInfo.isHu = true;
        huInfo.paixing[EHuType.HU.ordinal()] = 1;
        if (will == MahjongConstant.MJ_TWO_TIAO ||
                will == MahjongConstant.MJ_TWO_TONG ||
                will == MahjongConstant.MJ_FIVE_TIAO ||
                will == MahjongConstant.MJ_FIVE_TONG ||
                will == MahjongConstant.MJ_EIGHT_TIAO ||
                will == MahjongConstant.MJ_EIGHT_TONG) {
            huInfo.isWillTwoFiveEight = true;
        }
        int cnt = 1;
        int fang = 0;
        do {
            if (false && liang) {
                // 亮牌
                if (cnt < 2) {
                    cnt = 2;
                }
                huInfo.paixing[EHuType.LP.ordinal()] = 2;
            }
            if (isQiDui) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.QD);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.QD.ordinal()] = fang;
                huInfo.isQiDui = true;
            }
            boolean isPengPengHu = isQiDui ? false : isPengPengHu(temp4Cards.get());
            if (isPengPengHu) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.PPH);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.PPH.ordinal()] = fang;
                huInfo.isPengPengHu = true;
            }
            int isMingSiGui = isQiDui || isPengPengHu ? 0 : isMingSiGuiYi(card, temp5Cards.get(), cardValue, isFullChannel, huInfo.isZiMo);
            if (0 != isMingSiGui) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.MSG);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.MSG.ordinal()] = fang;
                if (isMingSiGui >= 2) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.MSG_S);
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.MSG.ordinal()] = 0;
                    huInfo.paixing[EHuType.MSG_S.ordinal()] = fang;
                }
            }
            int isAnSiGui = isQiDui || isPengPengHu ? 0 : isAnSiGuiYi(temp3Cards.get(), cardValue, isFullChannel);
            if (0 != isAnSiGui && fenSanDui) {
                isAnSiGui = isAnSiGuiYiWithFenSanDui(temp3Cards.get(), temp5Cards.get());
            }
            if (0 != isAnSiGui) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.ASG);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.ASG.ordinal()] = fang;
                if (isAnSiGui >= 2) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.ASG_S);
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.ASG.ordinal()] = 0;
                    huInfo.paixing[EHuType.ASG_S.ordinal()] = fang;
                }
            }
            if (0 != isMingSiGui && 0 != isAnSiGui) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.MSG_ASG);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.MSG.ordinal()] = 0;
                huInfo.paixing[EHuType.ASG.ordinal()] = 0;
                huInfo.paixing[EHuType.MSG_ASG.ordinal()] = fang;
            }
            boolean isShouZhuaYi = 2 == curCardCnt;
            if (isShouZhuaYi) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.SZY);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.PPH.ordinal()] = 0;
                huInfo.paixing[EHuType.SZY.ordinal()] = fang;
            }
            boolean isLongQiDui = isQiDui ? isLongQiDui(temp3Cards.get()) : false;
            if (isLongQiDui) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.LQD);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.QD.ordinal()] = 0;
                huInfo.paixing[EHuType.LQD.ordinal()] = fang;
            }
            boolean isShuangLongQiDui = isLongQiDui ? isShuangLongQiDui(temp3Cards.get()) : false;
            if (isShuangLongQiDui) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.SLQD);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.LQD.ordinal()] = 0;
                huInfo.paixing[EHuType.SLQD.ordinal()] = fang;
            }
            boolean isSanLongQiDui = isShuangLongQiDui ? isSanLongQiDui(temp3Cards.get()) : false;
            if (isSanLongQiDui) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.SANLQD);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.SLQD.ordinal()] = 0;
                huInfo.paixing[EHuType.SANLQD.ordinal()] = fang;
            }
            boolean isKWX = isQiDui || isPengPengHu ? false : isKWX(temp3Cards.get(), cardValue, cpgCnt);
            if (isKWX) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.KWX);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.KWX.ordinal()] = fang;
                huInfo.isKWX = true;
            }

            if (isKWX && 0 != isMingSiGui) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.KWX_MSG);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.KWX.ordinal()] = 0;
                huInfo.paixing[EHuType.MSG.ordinal()] = 0;
                huInfo.paixing[EHuType.KWX_MSG.ordinal()] = fang;
                if (isMingSiGui >= 2) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.KWX_MSG_S);
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.KWX_MSG.ordinal()] = 0;
                    huInfo.paixing[EHuType.MSG_S.ordinal()] = 0;
                    huInfo.paixing[EHuType.KWX_MSG_S.ordinal()] = fang;
                }
            }

            if (isKWX && 0 != isAnSiGui) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.KWX_ASG);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.KWX.ordinal()] = 0;
                huInfo.paixing[EHuType.ASG.ordinal()] = 0;
                huInfo.paixing[EHuType.KWX_ASG.ordinal()] = fang;
                if (isAnSiGui >= 2) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.KWX_ASG_S);
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.KWX_ASG.ordinal()] = 0;
                    huInfo.paixing[EHuType.ASG_S.ordinal()] = 0;
                    huInfo.paixing[EHuType.KWX_ASG_S.ordinal()] = fang;
                }
            }

            if (isKWX && 0 != isMingSiGui && 0 != isAnSiGui) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.KWX_MSG_ASG);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.KWX_MSG.ordinal()] = 0;
                huInfo.paixing[EHuType.KWX_ASG.ordinal()] = 0;
                huInfo.paixing[EHuType.MSG_ASG.ordinal()] = 0;
                huInfo.paixing[EHuType.KWX_MSG_ASG.ordinal()] = fang;
            }

            if (isQiDui && !isLongQiDui && isXiaoSanYuanQiDui(temp4Cards.get())) {
                huInfo.isXiaoSanYuanQiDui = true;
            }

            boolean isQingYiSe = isQingYiSe(temp4Cards.get());
            if (isQingYiSe) {
                int mul = 1;
                if (1 == city) {
                    int xiaoGangQYS = isXiaoGangQYS(card, temp5Cards.get());
                    if (xiaoGangQYS > 0) {
                        mul = (int) Math.pow(2, xiaoGangQYS);
                    }
                }
                fang = TbKWXFangManager.I.getFang(city, EHuType.QYS) * mul;
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.QYS.ordinal()] = fang;
                if (isPengPengHu) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_PPH) * mul;
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.PPH.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_PPH.ordinal()] = fang;
                }
                if (isQiDui) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_QD) * mul;
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.QD.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_QD.ordinal()] = fang;
                }
                if (isShouZhuaYi) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_SZY) * mul;
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.SZY.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_PPH.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_SZY.ordinal()] = fang;
                }
                if (isKWX) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_KWX) * mul;
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.KWX.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_KWX.ordinal()] = fang;
                }
                if (0 != isMingSiGui) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_MSG) * mul;
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.MSG.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_MSG.ordinal()] = fang;
                    if (isMingSiGui >= 2) {
                        fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_MSG_S) * mul;
                        if (cnt < fang) {
                            cnt = fang;
                        }
                        huInfo.paixing[EHuType.QYS_MSG.ordinal()] = 0;
                        huInfo.paixing[EHuType.QYS_MSG_S.ordinal()] = fang;
                    }
                }
                if (0 != isAnSiGui) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_ASG) * mul;
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.ASG.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_ASG.ordinal()] = fang;
                    if (isAnSiGui >= 2) {
                        fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_ASG_S) * mul;
                        if (cnt < fang) {
                            cnt = fang;
                        }
                        huInfo.paixing[EHuType.QYS_ASG.ordinal()] = 0;
                        huInfo.paixing[EHuType.QYS_ASG_S.ordinal()] = fang;
                    }
                }
                if (0 != isMingSiGui && 0 != isAnSiGui) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_MSG_ASG) * mul;
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.QYS_MSG.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_ASG.ordinal()] = 0;
                    huInfo.paixing[EHuType.MSG_ASG.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_MSG_ASG.ordinal()] = fang;
                }
                //if (isPengPengHu || isQiDui || isShouZhuaYi || isKWX || 0 != isMingSiGui || 0 != isAnSiGui) {
                //    if (cnt < 8) {
                //        cnt = 8;
                //    }
                //}
                //if (2 == isMingSiGui || 2 == isAnSiGui || (0 != isMingSiGui && 0 != isAnSiGui)) {
                //    if (cnt < 16) {
                //        cnt = 16;
                //    }
                //}
                if (isLongQiDui) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_LQD) * mul;
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.QYS.ordinal()] = 0;
                    huInfo.paixing[EHuType.LQD.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_QD.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_LQD.ordinal()] = fang;
                }
                if (isKWX) {
                    if (0 != isMingSiGui) {
                        fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_MSG_KWX) * mul;
                        if (cnt < fang) {
                            cnt = fang;
                        }
                        huInfo.paixing[EHuType.QYS_MSG.ordinal()] = 0;
                        huInfo.paixing[EHuType.QYS_KWX.ordinal()] = 0;
                        huInfo.paixing[EHuType.KWX_MSG.ordinal()] = 0;
                        huInfo.paixing[EHuType.QYS_MSG_KWX.ordinal()] = fang;
                        if (isMingSiGui >= 2) {
                            fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_KWX_MSG_S) * mul;
                            if (cnt < fang) {
                                cnt = fang;
                            }
                            huInfo.paixing[EHuType.QYS_MSG_S.ordinal()] = 0;
                            huInfo.paixing[EHuType.QYS_MSG_KWX.ordinal()] = 0;
                            huInfo.paixing[EHuType.KWX_MSG_S.ordinal()] = 0;
                            huInfo.paixing[EHuType.QYS_KWX_MSG_S.ordinal()] = fang;
                        }
                    }
                    if (0 != isAnSiGui) {
                        fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_ASG_KWX) * mul;
                        if (cnt < fang) {
                            cnt = fang;
                        }
                        huInfo.paixing[EHuType.QYS_ASG.ordinal()] = 0;
                        huInfo.paixing[EHuType.QYS_KWX.ordinal()] = 0;
                        huInfo.paixing[EHuType.KWX_ASG.ordinal()] = 0;
                        huInfo.paixing[EHuType.QYS_ASG_KWX.ordinal()] = fang;
                        if (isAnSiGui >= 2) {
                            fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_KWX_ASG_S) * mul;
                            if (cnt < fang) {
                                cnt = fang;
                            }
                            huInfo.paixing[EHuType.QYS_ASG_S.ordinal()] = 0;
                            huInfo.paixing[EHuType.QYS_ASG_KWX.ordinal()] = 0;
                            huInfo.paixing[EHuType.KWX_ASG_S.ordinal()] = 0;
                            huInfo.paixing[EHuType.QYS_KWX_ASG_S.ordinal()] = fang;
                        }
                    }
                    if (0 != isMingSiGui && 0 != isAnSiGui) {
                        fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_KWX_MSG_ASG) * mul;
                        if (cnt < fang) {
                            cnt = fang;
                        }
                        huInfo.paixing[EHuType.QYS_MSG_KWX.ordinal()] = 0;
                        huInfo.paixing[EHuType.QYS_ASG_KWX.ordinal()] = 0;
                        huInfo.paixing[EHuType.QYS_MSG_ASG.ordinal()] = 0;
                        huInfo.paixing[EHuType.KWX_MSG_ASG.ordinal()] = 0;
                        huInfo.paixing[EHuType.QYS_KWX_MSG_ASG.ordinal()] = fang;
                    }
                }
                if (isShuangLongQiDui) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_SLQD) * mul;
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.QYS.ordinal()] = 0;
                    huInfo.paixing[EHuType.SLQD.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_LQD.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_SLQD.ordinal()] = fang;
                }
                if (isSanLongQiDui) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.QYS_SANLQD) * mul;
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.QYS.ordinal()] = 0;
                    huInfo.paixing[EHuType.SANLQD.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_SLQD.ordinal()] = 0;
                    huInfo.paixing[EHuType.QYS_SANLQD.ordinal()] = fang;
                }
                break;
            }
            boolean isXiaoSanYuan = isXiaoSanYuan(temp4Cards.get());
            if (isXiaoSanYuan) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.XSY);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.XSY.ordinal()] = fang;
                huInfo.isXiaoSanYuan = true;
                if (isPengPengHu) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.XSY_PPH);
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.XSY.ordinal()] = 0;
                    huInfo.paixing[EHuType.PPH.ordinal()] = 0;
                    huInfo.paixing[EHuType.XSY_PPH.ordinal()] = fang;
                }
                if (isShouZhuaYi) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.XSY_SZY);
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.XSY.ordinal()] = 0;
                    huInfo.paixing[EHuType.SZY.ordinal()] = 0;
                    huInfo.paixing[EHuType.XSY_PPH.ordinal()] = 0;
                    huInfo.paixing[EHuType.XSY_SZY.ordinal()] = fang;
                }
                if (0 != isMingSiGui) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.XSY_MSG);
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.XSY.ordinal()] = 0;
                    huInfo.paixing[EHuType.MSG.ordinal()] = 0;
                    huInfo.paixing[EHuType.XSY_MSG.ordinal()] = fang;
                }
                if (0 != isAnSiGui) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.XSY_ASG);
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.XSY.ordinal()] = 0;
                    huInfo.paixing[EHuType.ASG.ordinal()] = 0;
                    huInfo.paixing[EHuType.XSY_ASG.ordinal()] = fang;
                }
                if (isKWX) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.XSY_KWX);
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.XSY.ordinal()] = 0;
                    huInfo.paixing[EHuType.KWX.ordinal()] = 0;
                    huInfo.paixing[EHuType.XSY_KWX.ordinal()] = fang;
                }
                //if (isPengPengHu || isShouZhuaYi || 0 != isMingSiGui || 0 != isAnSiGui || isKWX) {
                //    if (cnt < 8) {
                //        cnt = 8;
                //    }
                //}
                if (0 != isMingSiGui && isKWX) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.XSY_MSG_KWX);
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.XSY.ordinal()] = 0;
                    huInfo.paixing[EHuType.XSY_MSG.ordinal()] = 0;
                    huInfo.paixing[EHuType.XSY_KWX.ordinal()] = 0;
                    huInfo.paixing[EHuType.KWX_MSG.ordinal()] = 0;
                    huInfo.paixing[EHuType.XSY_MSG_KWX.ordinal()] = fang;
                }
                if (0 != isAnSiGui && isKWX) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.XSY_ASG_KWX);
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.XSY.ordinal()] = 0;
                    huInfo.paixing[EHuType.XSY_ASG.ordinal()] = 0;
                    huInfo.paixing[EHuType.XSY_KWX.ordinal()] = 0;
                    huInfo.paixing[EHuType.KWX_ASG.ordinal()] = 0;
                    huInfo.paixing[EHuType.XSY_ASG_KWX.ordinal()] = fang;
                }
                break;
            }
            boolean isDaSanYuan = isDaSanYuan(temp4Cards.get());
            if (isDaSanYuan) {
                fang = TbKWXFangManager.I.getFang(city, EHuType.DSY);
                if (cnt < fang) {
                    cnt = fang;
                }
                huInfo.paixing[EHuType.DSY.ordinal()] = fang;
                if (isPengPengHu) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.DSY_PPH);
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.DSY.ordinal()] = 0;
                    huInfo.paixing[EHuType.PPH.ordinal()] = 0;
                    huInfo.paixing[EHuType.DSY_PPH.ordinal()] = fang;
                }
                if (isShouZhuaYi) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.DSY_SZY);
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.DSY.ordinal()] = 0;
                    huInfo.paixing[EHuType.SZY.ordinal()] = 0;
                    huInfo.paixing[EHuType.DSY_PPH.ordinal()] = 0;
                    huInfo.paixing[EHuType.DSY_SZY.ordinal()] = fang;
                }
                if (isKWX) {
                    fang = TbKWXFangManager.I.getFang(city, EHuType.DSY_KWX);
                    if (cnt < fang) {
                        cnt = fang;
                    }
                    huInfo.paixing[EHuType.DSY.ordinal()] = 0;
                    huInfo.paixing[EHuType.KWX.ordinal()] = 0;
                    huInfo.paixing[EHuType.DSY_KWX.ordinal()] = fang;
                }
                //if (isPengPengHu || isShouZhuaYi || isKWX) {
                //    if (cnt < 16) {
                //        cnt = 16;
                //    }
                //}
                break;
            }
        } while (false);
        return cnt;
    }

    public static boolean isHu(byte[] card, int dep, boolean sevenPair) {
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = card[i];
        }
        if (sevenPair && isQiDui(tempCards.get())) {
            return true;
        }
        return -1 != isHu(tempCards.get(), dep);
    }

    public static boolean isHu(byte[] card) {
        return -1 != isHu(card, 0);
    }

    public static byte isHu(byte[] card, int dep) {
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (card[i] >= 2) {
                card[i] -= 2;
                if (dep >= 4) {
                    return (byte) i;
                }
                if (searchHu(card, dep)) {
                    return (byte) i;
                }
                card[i] += 2;
            }
        }
        return -1;
    }

    private static boolean searchHu(byte[] card, int dep) {
        // 刻字
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (card[i] >= 3) {
                if (dep >= 3) {
                    return true;
                }
                card[i] -= 3;
                if (searchHu(card, dep + 1)) {
                    return true;
                }
                card[i] += 3;
            }
        }
        // 顺子
        for (int i = 0; i < 25; ++i) {
            if (i % 9 < 7 && card[i + 1] >= 1 && card[i + 2] >= 1 && card[i + 3] >= 1) {
                if (dep >= 3) {
                    return true;
                }
                card[i + 1]--;
                card[i + 2]--;
                card[i + 3]--;
                if (searchHu(card, dep + 1)) {
                    return true;
                }
                card[i + 1]++;
                card[i + 2]++;
                card[i + 3]++;
            }
        }
        return false;
    }

    public static boolean isPengPengHu(byte[] card) {
        // 碰碰胡
        int two = 0;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (1 == card[i]) {
                return false;
            }
            if (4 == card[i]) {
                return false;
            }
            if (2 == card[i]) {
                ++two;
                if (two > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isQingYiSe(byte[] card) {
        // 清一色
        int wang = 0;
        int tiao = 0;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            byte value = card[i];
            if (value < 1) {
                continue;
            }
            if (i < 10) {
                wang = 1;
            } else if (i < 19) {
                if (0 != wang) {
                    return false;
                }
                tiao = 1;
            } else if (i < 28) {
                if (0 != tiao) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public static int isMingSiGuiYi(byte[] card, byte[] show2Card, byte huCard, boolean isFullChannel, boolean isZiMo) {
        // 明四归一
        if (isFullChannel) {
            // 全频道
            int cnt = 0;
            if (!isZiMo && show2Card[huCard] > 0) {
                ++cnt;
            }
            for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
                if (show2Card[i] >= 3 && card[i] > 0) {
                    ++cnt;
                }
            }
            return cnt;
        } else {
            // 半频道
            return show2Card[huCard] > 0 ? 1 : 0;
        }
    }

    public static int isAnSiGuiYi(byte[] card, byte huCard, boolean isFullChannel) {
        // 暗四归一
        if (isFullChannel) {
            // 全频道
            int cnt = 0;
            for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
                if (card[i] == 4) {
                    ++cnt;
                }
            }
            return cnt;
        } else {
            // 半频道
            return card[huCard] == 4 ? 1 : 0;
        }
    }

    public static int isAnSiGuiYiWithFenSanDui(byte[] card, byte[] cpgCard) {
        int dep = 0;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            tempCards.get()[i] = card[i];
            if (cpgCard[i] >= 3) {
                ++dep;
            }
        }
        int cnt = 0;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (tempCards.get()[i] == 4) {
                tempCards.get()[i] -= 3;
                if (-1 != isHu(tempCards.get(), dep + 1)) {
                    ++cnt;
                }
                tempCards.get()[i] += 3;
            }
        }
        return cnt;
    }

    public static int isXiaoGangQYS(byte[] card, byte[] show2Card) {
        // 明四归一
        int cnt = 0;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (4 == card[i] || (show2Card[i] >= 3 && card[i] > 0)) {
                ++cnt;
            }
        }
        return cnt;
    }

    public static boolean isQiDui(byte[] card) {
        // 7对
        int cnt = 0;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            byte value = card[i];
            if (2 == value) {
                ++cnt;
            } else if (4 == value) {
                cnt += 2;
            }
            if (1 == value || 3 == value) {
                return false;
            }
        }
        return 7 == cnt;
    }

    public static boolean isLongQiDui(byte[] card) {
        // 豪华7对
        // 先对7对判断
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (4 == card[i]) {
                return true;
            }
        }
        return false;
    }

    public static boolean isShuangLongQiDui(byte[] card) {
        // 双豪华7对
        // 先对7对判断
        int cnt = 0;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (4 == card[i]) {
                ++cnt;
            }
        }
        return cnt >= 2;
    }

    public static boolean isSanLongQiDui(byte[] card) {
        // 三豪华7对
        // 先对7对判断
        int cnt = 0;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            if (4 == card[i]) {
                ++cnt;
            }
        }
        return cnt >= 3;
    }

    public static boolean isDaSanYuan(byte[] card) {
        // 大三元
        byte zhong = card[MahjongConstant.MJ_ZHONG];
        byte fa = card[MahjongConstant.MJ_FA];
        byte bai = card[MahjongConstant.MJ_BAI];
        return zhong == 3 && fa == 3 && bai == 3;
    }

    public static boolean isXiaoSanYuan(byte[] card) {
        // 小三元
        byte zhong = card[MahjongConstant.MJ_ZHONG];
        byte fa = card[MahjongConstant.MJ_FA];
        byte bai = card[MahjongConstant.MJ_BAI];
        if (8 != (zhong + fa + bai)) {
            return false;
        }
        for (int i = MahjongConstant.MJ_ZHONG; i <= MahjongConstant.MJ_BAI; ++i) {
            if (0 == card[i] || 1 == card[i] || 4 == card[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean isXiaoSanYuanQiDui(byte[] card) {
        // 小三阳七对
        // 先判断七对
        byte zhong = card[MahjongConstant.MJ_ZHONG];
        byte fa = card[MahjongConstant.MJ_FA];
        byte bai = card[MahjongConstant.MJ_BAI];
        return 2 == zhong && 2 == fa && 2 == bai;
    }

    public static boolean isShouZhuaYi(byte[] card) {
        // 先判断胡了
        // 手抓一
        int kind = 0;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            byte value = card[i];
            if (value > 0) {
                if (2 != value) {
                    return false;
                }
                ++kind;
                if (kind > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isKWX(byte[] card, byte huCard, int dep) {
        // 卡五星
        if (huCard != MahjongConstant.MJ_FIVE_TIAO && huCard != MahjongConstant.MJ_FIVE_TONG) {
            return false;
        }
        if (card[huCard] < 1 || card[huCard - 1] < 1 || card[huCard + 1] < 1) {
            return false;
        }
        byte[] temp = new byte[MahjongConstant.MJ_CARD_KINDS];
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            temp[i] = card[i];
        }
        temp[huCard]--;
        temp[huCard - 1]--;
        temp[huCard + 1]--;
        return -1 != isHu(temp, dep + 1);
    }

    public static boolean isBump(byte[] cards, byte cardValue) {
        // 碰
        return cards[cardValue] >= 2;
    }

    //public static String card2Client(byte[] cards) {
    //    StringBuffer sb = new StringBuffer();
    //    boolean first = true;
    //    for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
    //        byte value = cards[i];
    //        if (value < 1) {
    //            continue;
    //        }
    //        for (int j = 0; j < value; ++j) {
    //            if (!first) {
    //                sb.append(",");
    //            }
    //            sb.append(i);
    //            first = false;
    //        }
    //    }
    //    return sb.toString();
    //}

    //public static String card2Client(byte[] cards, byte fumble, boolean addLast) {
    //    StringBuffer sb = new StringBuffer();
    //    boolean first = true;
    //    for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
    //        byte value = cards[i];
    //        if (value < 1) {
    //            continue;
    //        }
    //        if (fumble == i) {
    //            --value;
    //        }
    //        for (int j = 0; j < value; ++j) {
    //            if (!first) {
    //                sb.append(",");
    //            }
    //            sb.append(i);
    //            first = false;
    //        }
    //    }
    //    if (addLast) {
    //        sb.append(",");
    //        sb.append(fumble);
    //    }
    //    return sb.toString();
    //}

    //public static String card2Client(List<Byte> cards) {
    //    StringBuffer sb = new StringBuffer();
    //    boolean first = true;
    //    for (Byte c : cards) {
    //        if (!first) {
    //            sb.append(",");
    //        }
    //        sb.append(c);
    //        first = false;
    //    }
    //    return sb.toString();
    //}

    public static String card2Debug(byte[] cards) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 1; i < MahjongConstant.MJ_CARD_KINDS; ++i) {
            byte value = cards[i];
            if (value < 1) {
                continue;
            }
            for (int j = 0; j < value; ++j) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(MahjongConstant.CARDS[i]);
                first = false;
            }
        }
        return sb.toString();
    }

    public static String card2Debug(List<Byte> cards) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Byte c : cards) {
            if (!first) {
                sb.append(",");
            }
            sb.append(MahjongConstant.CARDS[c]);
            first = false;
        }
        return sb.toString();
    }
}
