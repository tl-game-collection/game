package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongHuanPai;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MahjongHuanPaiAction extends BaseMahjongAction {
    protected static int[] NI_4 = new int[]{3, 2, 1, 0};
    protected static int[] SHUN_4 = new int[]{1, 2, 3, 0};
    protected static int[] NI_3 = new int[]{2, 1, 0};
    protected static int[] SHUN_3 = new int[]{1, 2, 0};
    protected static int[] NI_2 = new int[]{1, 0};
    protected static int[] SHUN_2 = new int[]{1, 0};
    protected static int[] DUI = new int[]{2, 3, 0, 1};

    protected HashMap<Long, Integer> allPlayer = new HashMap<>();
    protected int cnt = 0;

    public MahjongHuanPaiAction(IRoom room, long timeout) {
        super(room, EActionOp.HUAN_PAI, timeout);
    }

    public void addPlayer(IMahjongPlayer player) {
        this.allPlayer.put(player.getUid(), -1);
    }

    public ErrorCode huanPai(IMahjongPlayer player, int card) {
        if (-1 != this.allPlayer.getOrDefault(player.getUid(), -1)) {
            return ErrorCode.PLAYER_ALREADY_OPERATE;
        }
        byte card1 = (byte) (card & 0x3F);
        byte card2 = (byte) ((card >> 6) & 0x3F);
        byte card3 = (byte) ((card >> 12) & 0x3F);

        int color1 = MahjongUtil.getColor(card1);
        int color2 = MahjongUtil.getColor(card2);
        int color3 = MahjongUtil.getColor(card3);

        if (3 * color1 != (color1 + color2 + color3)) {
            return ErrorCode.REQUEST_INVALID_DATA;
        }

        if (!player.hasHandCard(card1, card2, card3)) {
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        player.delHandCard(card1);
        player.delHandCard(card2);
        player.delHandCard(card3);
        this.allPlayer.put(player.getUid(), card);
        ++this.cnt;
        ((IMahjongHuanPai) this.room).doSendHuanPaiInfo(player);
        return ErrorCode.OK;
    }

    public IRoomPlayer getLastRoomPlayer(int index) {
        IRoomPlayer player = null;
        int loop = 0;
        int num=this.room.getMaxPlayerCnt();
        do {
            if (loop > this.room.getMaxPlayerCnt()) {
                return null;
            }
            player = this.room.getRoomPlayer((--index+num)%num);
            ++loop;
        } while (null == player || player.isGuest() || player.isOver());
        return player;
    }

    public void getSendHuanPai(int card,IMahjongPlayer p1,int type){
        if (-1 == card) {
            Logs.ROOM.error("%s 无效换牌", this.room);
            return;
        }
        byte card1 = (byte) (card & 0x3F);
        byte card2 = (byte) ((card >> 6) & 0x3F);
        byte card3 = (byte) ((card >> 12) & 0x3F);
        p1.addHandCard(card1);
        p1.addHandCard(card2);
        p1.addHandCard(card3);
        ((IMahjongHuanPai) this.room).doSendEndHuanPai(p1, type, this.allPlayer.get(p1.getUid()), card, 3);
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
                }
            }

            ((IMahjongHuanPai) this.room).endHuanPai();
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        for (Map.Entry<Long, Integer> entry : this.allPlayer.entrySet()) {
            ((IMahjongHuanPai) this.room).doSendBeginHuanPai((IMahjongPlayer) this.room.getRoomPlayer(entry.getKey()), entry.getValue(), 3);
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        if (this.allPlayer.containsKey(player.getUid())) {
            ((IMahjongHuanPai) this.room).doSendBeginHuanPai((IMahjongPlayer) player, this.allPlayer.get(player.getUid()), 3);
        }
    }
}
