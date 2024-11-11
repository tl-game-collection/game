package com.xiuxiu.app.server.room.normal.poker.cow;

import java.util.List;

import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.GameInfo;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.player.IPokerPlayer;

/**
 * 明牌抢庄
 * @auther: yuyunfei
 * @date: 2020/1/6 17:10
 * @comment:
 */
@GameInfo(gameType = GameType.GAME_TYPE_COW,gameSubType = 2)
public class CowRoom extends AbstractCowRoom {
    public CowRoom(RoomInfo roomInfo) {
        this(roomInfo, ERoomType.NORMAL);
    }

    public CowRoom(RoomInfo roomInfo, ERoomType roomType) {
        super(roomInfo, roomType);
    }


    @Override
    public void init() {
        super.init();
        //this.autoReady = false;
        //this.autoReady = true;
        this.autoReady = false;
    }
    /**
     * 明牌抢庄 检查下注值是否正确
     */
    @Override
    public boolean checkRebValue(IPokerPlayer pokerPlayer, int rebValue,int pushNoteValue,boolean isDoubling){
        if (rebValue <= 0){
            return false;
        }
        //创建包厢房间的时候录入,1/2分0.5倍录入5/10
        List<Integer> baseRebet = this.getCowInfo().getBaseReb();
        if (baseRebet.contains(rebValue)){
            return true;
        }

        if (rebValue == pushNoteValue){
            return true;
        }

        if (isDoubling && baseRebet.contains(rebValue/2)){
            return true;
        }
        return false;
    }

    /**
     * 游戏分兑换竞技分
     * @return
     */
    @Override
    protected int getScore(int value) {
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * 100 * value / 10;
    }

}
