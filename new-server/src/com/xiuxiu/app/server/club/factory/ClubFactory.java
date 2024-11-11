package com.xiuxiu.app.server.club.factory;

import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.club.impl.ClubCard;
import com.xiuxiu.app.server.club.impl.ClubGold;

/**
 * 亲友圈实例创建简单工厂类
 * 
 * @author Administrator
 *
 */
public final class ClubFactory {

    /**
     * 创建亲友圈
     * 
     * @param clubType
     * @return
     */
    public static IClub createClub(EClubType clubType) {
        switch (clubType) {
        case CARD:
            return new ClubCard();
        case GOLD:
            return new ClubGold();
        default:
            break;
        }
        return null;
    }
}
