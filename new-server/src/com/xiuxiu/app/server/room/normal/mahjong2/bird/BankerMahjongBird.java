package com.xiuxiu.app.server.room.normal.mahjong2.bird;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

import java.util.HashMap;
import java.util.List;

public class BankerMahjongBird extends BaseMahjongBird {
    private HashMap<Integer, Integer> allHit = new HashMap<>();

    @Override
    public boolean isAllHit(int index1, int index2) {
        return this.birdCnt == this.allHit.getOrDefault(index1, 0) + this.allHit.getOrDefault(index2, 0);
    }

    @Override
    public boolean isAllMiss(int index1, int index2) {
        return 0 == this.allHit.getOrDefault(index1, 0) + this.allHit.getOrDefault(index2, 0);
    }

    @Override
    public boolean isHit(IMahjongRoom room, IMahjongPlayer player, byte card) {
        if (card == MahjongUtil.MJ_Z_FENG) {
            ++this.allNiaoCnt;
            return true;
        }
        List<IRoomPlayer> list = room.getCurrPlayers();
        int index = -1;
        IRoomPlayer banker = room.getRoomPlayer(room.getBankerIndex());
        for(int i =0;i<list.size();i++)
        {
        	if(banker.getUid()==list.get(i).getUid())
        	{
        		index=i;
        		break;
        	}
        }
        int score = this.getScore(card);
        int hitIndex = (index + score - 1) % list.size();
        int hitCnt = this.allHit.getOrDefault(hitIndex, 0);
        this.allHit.put(hitIndex, hitCnt + 1);
        IRoomPlayer hitRoomPlayer=list.get(hitIndex);
        hitRoomPlayer.addScore(Score.MJ_CUR_NIAO_HIT_SCORE, 1, false);
        hitRoomPlayer.addScore(Score.MJ_CUR_NIAO_HIT_NUM, 1, false);
        return false;
    }
}
