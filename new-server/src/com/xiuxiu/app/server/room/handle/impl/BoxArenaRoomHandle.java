package com.xiuxiu.app.server.room.handle.impl;

import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.poker.cow.AbstractCowRoom;
import com.xiuxiu.app.server.room.player.poker.FGFPlayer;
import com.xiuxiu.app.server.room.player.poker.SGPlayer;
import com.xiuxiu.app.server.room.player.poker.cow.CowPlayer;
import com.xiuxiu.app.server.score.ScoreItemInfo;

/**
 * 包厢竞技场业务扩展处理器
 *
 * @author Administrator
 */
public class BoxArenaRoomHandle extends AbstractBoxArenaRoomHandle {

    public BoxArenaRoomHandle(IRoom room, Box box) {
        super(room, box);
    }

    @Override
    protected ScoreItemInfo buildScoreItemInfo(IRoomPlayer temp) {
        ScoreItemInfo itemInfo = new ScoreItemInfo();
        itemInfo.setScore(this.room.getRecordScore(temp));
        itemInfo.setPlayerUid(temp.getUid());

        if (this.room.getGameType() == GameType.GAME_TYPE_COW) {
            CowPlayer cowPlayer = (CowPlayer)temp;
            itemInfo.setCardType(cowPlayer.getCurCardType().getValue());
            itemInfo.setLastCard(cowPlayer.getLastCard());
            itemInfo.setBnaker(this.room.getBankerIndex()==cowPlayer.getIndex());
            itemInfo.setBankerMul(cowPlayer.getScore(Score.POKER_COW_ROB_BANKER_MUL,false));
            itemInfo.setPushMul(cowPlayer.getScore(Score.POKER_COW_REBET,false));
            if (cowPlayer.getResultCard() != null) {
                itemInfo.getCard().addAll(cowPlayer.getResultCard());
                itemInfo.getTailCard().addAll(cowPlayer.getHandCard());
            }
            itemInfo.setMonsterType(((AbstractCowRoom)this.room).getCowInfo().getLaiZiCard());
        }
        if (this.room.getGameType() == GameType.GAME_TYPE_FRIED_GOLDEN_FLOWER) {
            FGFPlayer fgfPlayer = (FGFPlayer)temp;
            itemInfo.setCardType(fgfPlayer.getCurCardType().getValue());
            itemInfo.setDiscard(fgfPlayer.isDiscard());
            itemInfo.setWin(fgfPlayer.isWin());
            if (fgfPlayer.getHandCard() != null) {
                itemInfo.getCard().addAll(fgfPlayer.getHandCard());
            }
        }
        if (this.room.getGameType() == GameType.GAME_TYPE_SG){
            SGPlayer sgPlayer = (SGPlayer)temp;
            itemInfo.setCardType(sgPlayer.getCurCardType().getValue());
            itemInfo.setCardTypeExtra(sgPlayer.getCurCardTypeExtra().ordinal());
            itemInfo.setBnaker(this.room.getBankerIndex()==sgPlayer.getIndex());
            if (sgPlayer.getHandCard() != null) {
                itemInfo.getCard().addAll(sgPlayer.getHandCard());
            }
        }
        return itemInfo;
    }

}
