package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongHuanPai;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.YCXLMahjongPlayer;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.HashMap;
import java.util.Map;

public class MahjongHuanPaiActionByYCXL extends MahjongHuanPaiAction {

    public MahjongHuanPaiActionByYCXL(IRoom room, long timeout) {
        super(room, timeout);
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            for (Map.Entry<Long, Integer> entry : this.allPlayer.entrySet()) {
                if (-1 != entry.getValue()) {
                    continue;
                }
                IMahjongPlayer player = (IMahjongPlayer) this.room.getRoomPlayer(entry.getKey());

                int card = player.getMinSameColorCard(3);
                byte card1 = (byte) (card & 0x3F);
                byte card2 = (byte) ((card >> 6) & 0x3F);
                byte card3 = (byte) ((card >> 12) & 0x3F);

                player.delHandCard(card1);
                player.delHandCard(card2);
                player.delHandCard(card3);
                entry.setValue(card);
            }
            this.cnt = this.allPlayer.size();
        }
        if (this.cnt >= this.allPlayer.size()) {
            int type = 0;
            int num = this.room.getCurPlayerCnt();
            if (num <= 3) {
                type = RandomUtil.random(0, 1);
            } else {
                type = RandomUtil.random(0, 2);
            }
            int[] temp = null;
            if (0 == type) {
                // 顺时针
                if (num <= 2) {
                    temp = SHUN_2;
                } else if (num <= 3) {
                    temp = SHUN_3;
                } else {
                    temp = SHUN_4;
                }
            } else if (1 == type) {
                // 逆时针
                if (num <= 2) {
                    temp = NI_2;
                } else if (num <= 3) {
                    temp = NI_3;
                } else {
                    temp = NI_4;
                }
            } else if (2 == type) {
                // 对家
                temp = DUI;
            }
            if(num<=3){
                if(type==0){
                    if(num==2){
                        IMahjongPlayer p1=null;
                        IMahjongPlayer p2=null;
                        p1 = (IMahjongPlayer) this.room.getRoomPlayer(0);
                        if(p1==null){
                            p1=(IMahjongPlayer)this.getLastRoomPlayer(0);
                        }
                        p2 = (IMahjongPlayer) this.getLastRoomPlayer(p1.getIndex());
                        int card1 = this.allPlayer.get(p2.getUid());
                        int card2 = this.allPlayer.get(p1.getUid());
                        this.getSendHuanPai(card1,p1,0);
                        this.getSendHuanPai(card2,p2,0);
                        ((YCXLMahjongPlayer)p1).addHuanPai(p1.getUid(),p2.getUid(),card2,card1,0);
                        ((YCXLMahjongPlayer)p2).addHuanPai(p2.getUid(),p1.getUid(),card1,card2,0);
                    }else{
                        IMahjongPlayer p1=null;
                        IMahjongPlayer p2=null;
                        IMahjongPlayer p3=null;
                        p1 = (IMahjongPlayer) this.room.getRoomPlayer(0);
                        if(p1==null){
                            p1=(IMahjongPlayer)this.getLastRoomPlayer(0);
                        }
                        p2 = (IMahjongPlayer) this.getLastRoomPlayer(p1.getIndex());
                        p3 = (IMahjongPlayer) this.getLastRoomPlayer(p2.getIndex());
                        int card1 = this.allPlayer.get(p3.getUid());
                        int card2 = this.allPlayer.get(p1.getUid());
                        int card3 = this.allPlayer.get(p2.getUid());
                        this.getSendHuanPai(card1,p1,0);
                        this.getSendHuanPai(card2,p2,0);
                        this.getSendHuanPai(card3,p3,0);
                        ((YCXLMahjongPlayer)p1).addHuanPai(p1.getUid(),p3.getUid(),card2,card1,0);
                        ((YCXLMahjongPlayer)p2).addHuanPai(p2.getUid(),p1.getUid(),card3,card2,0);
                        ((YCXLMahjongPlayer)p3).addHuanPai(p3.getUid(),p2.getUid(),card1,card3,0);

                    }
                }else{
                    if(num==2){
                        IMahjongPlayer p1=null;
                        IMahjongPlayer p2=null;
                        p1 = (IMahjongPlayer) this.room.getRoomPlayer(0);
                        if(p1==null){
                            p1=(IMahjongPlayer)this.room.getNextRoomPlayer(0);
                        }
                        p2 = (IMahjongPlayer) this.room.getNextRoomPlayer(p1.getIndex());
                        int card1 = this.allPlayer.get(p2.getUid());
                        int card2 = this.allPlayer.get(p1.getUid());
                        this.getSendHuanPai(card1,p1,1);
                        this.getSendHuanPai(card2,p2,1);
                        ((YCXLMahjongPlayer)p1).addHuanPai(p1.getUid(),p2.getUid(),card2,card1,1);
                        ((YCXLMahjongPlayer)p2).addHuanPai(p2.getUid(),p1.getUid(),card1,card2,1);
                    }else{
                        IMahjongPlayer p1=null;
                        IMahjongPlayer p2=null;
                        IMahjongPlayer p3=null;
                        p1 = (IMahjongPlayer) this.room.getRoomPlayer(0);
                        if(p1==null){
                            p1=(IMahjongPlayer)this.room.getNextRoomPlayer(0);
                        }
                        p2 = (IMahjongPlayer) this.room.getNextRoomPlayer(p1.getIndex());
                        p3 = (IMahjongPlayer) this.room.getNextRoomPlayer(p2.getIndex());
                        int card1 = this.allPlayer.get(p3.getUid());
                        int card2 = this.allPlayer.get(p1.getUid());
                        int card3 = this.allPlayer.get(p2.getUid());
                        this.getSendHuanPai(card1,p1,1);
                        this.getSendHuanPai(card2,p2,1);
                        this.getSendHuanPai(card3,p3,1);
                        ((YCXLMahjongPlayer)p1).addHuanPai(p1.getUid(),p3.getUid(),card2,card1,1);
                        ((YCXLMahjongPlayer)p2).addHuanPai(p2.getUid(),p1.getUid(),card3,card2,1);
                        ((YCXLMahjongPlayer)p3).addHuanPai(p3.getUid(),p2.getUid(),card1,card3,1);
                    }
                }
            }else{
                for (int i = 0; i < num; ++i) {
                    IMahjongPlayer p1 = (IMahjongPlayer) this.room.getRoomPlayer(i);
                    IMahjongPlayer  p2 = (IMahjongPlayer) this.room.getRoomPlayer(temp[i]);
                    int card = this.allPlayer.get(p2.getUid());
                    if (-1 == card) {
                        Logs.ROOM.error("%s 无效换牌", this.room);
                        continue;
                    }
                    byte card1 = (byte) (card & 0x3F);
                    byte card2 = (byte) ((card >> 6) & 0x3F);
                    byte card3 = (byte) ((card >> 12) & 0x3F);
                    p1.addHandCard(card1);
                    p1.addHandCard(card2);
                    p1.addHandCard(card3);
                    ((IMahjongHuanPai) this.room).doSendEndHuanPai(p1, type, this.allPlayer.get(p1.getUid()), card, 3);
                    ((YCXLMahjongPlayer)p1).addHuanPai(p1.getUid(),p2.getUid(),this.allPlayer.get(p1.getUid()),card,type);
                }
            }

            ((IMahjongHuanPai) this.room).endHuanPai();
            return true;
        }
        return false;
    }
}
