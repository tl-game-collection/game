package com.xiuxiu.app.server.room.normal.mahjong.action;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfBeginHuanPai;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfEndHuanPai;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong.IMahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong.MahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong.MahjongPlayer;
import com.xiuxiu.app.server.room.record.mahjong.HuanPaiRecordAction;
import com.xiuxiu.app.server.room.record.mahjong.MahjongRecord;
import com.xiuxiu.core.utils.RandomUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MahjongHuanPaiWaitAction extends BaseMahjongAction {
    private static int[] NI_4 = new int[]{3, 2, 1, 0};
    private static int[] SHUN_4 = new int[]{1, 2, 3, 0};
    private static int[] NI_3 = new int[]{2, 1, 0};
    private static int[] SHUN_3 = new int[]{1, 2, 0};
    private static int[] DUI = new int[]{2, 3, 0, 1};

    private ConcurrentHashMap<Long, List<Byte>> playerOp = new ConcurrentHashMap<>();
    private HuanPaiRecordAction huanPaiRecordAction;
    private int cnt;
    private byte[] tempHandCard = new byte[MahjongUtil.MJ_CARD_KINDS];

    public MahjongHuanPaiWaitAction(IMahjongRoom room, MahjongPlayer roomPlayer, long timeout) {
        super(room, EActionOp.HUAN_PAI, roomPlayer, timeout);
        this.huanPaiRecordAction = ((MahjongRecord) room.getRecord()).addHuanPaiAction();
    }

    public void addPlayer(long uid) {
        this.playerOp.put(uid, Collections.EMPTY_LIST);
    }

    public ErrorCode huanPai(long uid, List<Byte> card) {
        MahjongPlayer player = (MahjongPlayer) this.room.getRoomPlayer(uid);
        if (null == player || player.isGuest()) {
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        List<Byte> v = this.playerOp.get(uid);
        if (Collections.EMPTY_LIST != v) {
            return ErrorCode.PLAYER_ALREADY_OPERATE;
        }
        byte[] handCard = player.getHandCard();
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            this.tempHandCard[i] = handCard[i];
        }
        int color = -1;
        for (Byte c : card) {
            if (-1 == color) {
                color = MahjongUtil.getColor(c);
            } else if (color != MahjongUtil.getColor(c)) {
                return ErrorCode.REQUEST_INVALID_DATA;
            }
            --this.tempHandCard[c];
            if (this.tempHandCard[c] < 0) {
                return ErrorCode.REQUEST_INVALID_DATA;
            }
        }
        for (Byte c : card) {
            player.delHandCard(c, 1);
        }
        this.playerOp.put(uid, card);
        this.huanPaiRecordAction.addHuanPai(uid, card);
        ++this.cnt;
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout || this.cnt >= this.playerOp.size()) {
            // exchange
            Iterator<Map.Entry<Long, List<Byte>>> it = this.playerOp.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, List<Byte>> entry = it.next();
                if (Collections.EMPTY_LIST == entry.getValue()) {
                    // TODO
                    MahjongPlayer player = (MahjongPlayer) this.room.getRoomPlayer(entry.getKey());
                    byte[] handCard = player.getHandCard();
                    int color = -1;
                    int colorCnt = 0;
                    int minColor = -1;
                    int minColorCnt = Integer.MAX_VALUE;
                    for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
                        if (handCard[i] < 1) {
                            continue;
                        }
                        int temp = MahjongUtil.getColor(handCard[i]);
                        if (-1 == color) {
                            color = temp;
                            colorCnt = handCard[i];
                        } else if (color != temp) {
                            if (-1 == minColor || (colorCnt < minColorCnt && colorCnt >= 3)) {
                                minColor = color;
                                minColorCnt = colorCnt;
                            }
                            color = temp;
                            colorCnt = handCard[i];
                        } else {
                            colorCnt += handCard[i];
                        }
                    }
                    if (colorCnt < minColorCnt && colorCnt >= 3) {
                        minColor = color;
                        minColorCnt = colorCnt;
                    }
                    if (minColorCnt < 3) {
                        Logs.ROOM.error("%s 换牌错误", this);
                        continue;
                    }
                    List<Byte> card = new ArrayList<>();
                    for (int i = 0, cnt = 3; i < MahjongUtil.MJ_CARD_KINDS && cnt > 0; ++i) {
                        if (handCard[i] < 1) {
                            continue;
                        }
                        int temp = MahjongUtil.getColor(handCard[i]);
                        if (minColor == temp) {
                            int min = handCard[i] < cnt ? handCard[i] : cnt;
                            player.delHandCard((byte) i, min);
                            cnt -= min;
                            for (int k = 0; k < min; ++k) {
                                card.add((byte) i);
                            }
                        }
                    }
                    entry.setValue(card);
                }
            }
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
                temp = num <= 3 ? SHUN_3 : SHUN_4;
            } else if (1 == type) {
                // 逆时针
                temp = num <= 3 ? NI_3 : NI_4;
            } else if (2 == type) {
                // 对家
                temp = DUI;
            }
            for (int i = 0; i < num; ++i) {
                MahjongPlayer player = (MahjongPlayer) this.room.getRoomPlayer(i);
                MahjongPlayer oPlayer = (MahjongPlayer) this.room.getRoomPlayer(temp[i]);
                List<Byte> card = this.playerOp.get(oPlayer.getUid());
                for (Byte c : card) {
                    player.addHandCard(c);
                }

                PCLIMahjongNtfEndHuanPai info = new PCLIMahjongNtfEndHuanPai();
                info.card.addAll(card);
                info.myCard.addAll(this.playerOp.get(player.getUid()));
                info.type = type;
                player.send(CommandId.CLI_NTF_MAHJONG_END_HUAN_PAI, info);
            }

            ((MahjongRoom) this.room).endHuanPai();
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        Iterator<Map.Entry<Long, List<Byte>>> it = this.playerOp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, List<Byte>> entry = it.next();
            PCLIMahjongNtfBeginHuanPai info = new PCLIMahjongNtfBeginHuanPai();
            List<Byte> card = entry.getValue();
            if (Collections.EMPTY_LIST != card) {
                info.card.addAll(card);
            }
            this.room.getRoomPlayer(entry.getKey()).send(CommandId.CLI_NTF_MAHJONG_BEGIN_HUAN_PAI, info);
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        PCLIMahjongNtfBeginHuanPai info = new PCLIMahjongNtfBeginHuanPai();
        List<Byte> card = this.playerOp.get(player.getUid());
        if (Collections.EMPTY_LIST != card) {
            info.card.addAll(card);
        }
        player.send(CommandId.CLI_NTF_MAHJONG_BEGIN_HUAN_PAI, info);
    }
}
