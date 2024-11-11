package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MahjongWaitAction extends BaseMahjongAction {
    public static class WaitInfo {
        protected long playerUid;
        protected int index;
        protected boolean bump;
        protected boolean bar;
        protected boolean eat;
        protected boolean hu;
        protected long timeout;
        protected EActionOp op = EActionOp.NORMAL;
        protected Object[] param;
        
        protected boolean closeYHBH;

        public boolean isCloseYHBH() {
            return closeYHBH;
        }

        public void setCloseYHBH(boolean closeYHBH) {
            this.closeYHBH = closeYHBH;
        }

        public long getPlayerUid() {
            return playerUid;
        }

        public void setPlayerUid(long playerUid) {
            this.playerUid = playerUid;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public boolean isBump() {
            return bump;
        }

        public void setBump(boolean bump) {
            this.bump = bump;
        }

        public boolean isBar() {
            return bar;
        }

        public void setBar(boolean bar) {
            this.bar = bar;
        }

        public boolean isEat() {
            return eat;
        }

        public void setEat(boolean eat) {
            this.eat = eat;
        }

        public boolean isHu() {
            return hu;
        }

        public void setHu(boolean hu) {
            this.hu = hu;
        }

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }

        public EActionOp getOp() {
            return op;
        }

        public void setOp(EActionOp op) {
            this.op = op;
        }

        public void setParam(Object... param) {
            this.param = param;
        }

        public Object[] getParam() {
            return param;
        }

        public EActionOp getRealOp() {
            if (EActionOp.NORMAL != this.op) {
                return this.op;
            }
            if (this.hu) {
                return EActionOp.HU;
            }
            if (this.bar) {
                return EActionOp.BAR;
            }
            if (this.bump) {
                return EActionOp.BUMP;
            }
            if (this.eat) {
                return EActionOp.EAT;
            }
            return EActionOp.PASS;
        }
    }

    protected IMahjongPlayer takePlayer;
    protected ArrayList<WaitInfo> allWait = new ArrayList<>();
    protected byte takeCard;
    protected long huIndex = 0;

    public MahjongWaitAction(IRoom room, IMahjongPlayer takePlayer) {
        super(room, EActionOp.WAIT, -1);
        this.takePlayer = takePlayer;
    }

    public void addWait(WaitInfo waitInfo) {
        this.allWait.add(waitInfo);
        if (waitInfo.isHu()) {
            this.huIndex |= (1 << waitInfo.getIndex());
        }
    }

    public void opWait(WaitInfo waitInfo, EActionOp op, Object... param) {
        waitInfo.setOp(op);
        if (null != param) {
            waitInfo.setParam(param);
        }
        if (waitInfo.isHu()) {
            // 可以胡
            if (EActionOp.HU != op) {
                // 不胡
                this.huIndex &= (~(1 << waitInfo.getIndex()));
            }
        }
    }

    public WaitInfo getWaitInfo(long playerUid) {
        for (WaitInfo info : this.allWait) {
            if (playerUid == info.getPlayerUid()) {
                return info;
            }
        }
        return null;
    }

    public boolean check() {
        Collections.sort(this.allWait, new Comparator<WaitInfo>() {
            @Override
            public int compare(WaitInfo o1, WaitInfo o2) {
                // > 0 o2 在前
                EActionOp op1 = o1.getRealOp();
                EActionOp op2 = o2.getRealOp();
                if (op1.equals(op2)) {
                    int a1 = o1.getIndex();
                    if (a1 < takePlayer.getIndex()) {
                        a1 += room.getMaxPlayerCnt();
                    }
                    int a2 = o2.getIndex();
                    if (a2 < takePlayer.getIndex()) {
                        a2 += room.getMaxPlayerCnt();
                    }
                    return a1 - a2;
                }
                return op2.ordinal() - op1.ordinal();
            }
        });

        EActionOp op = null;
        for (int i = 0, len = this.allWait.size(); i < len; ++i) {
            WaitInfo info = this.allWait.get(i);
            if (null != op && !op.equals(info.getRealOp())) {
                return true;
            }
            if (EActionOp.NORMAL == info.op) {
                return false;
            }
            if (null == op) {
                op = info.getRealOp();
            }
            if (EActionOp.HU.equals(op)) {
                if (!((IMahjongRoom) this.room).isMoreHu()) {
                    for (int j = i + 1; j < len; ++j) {
                        info = this.allWait.get(j);
                        if (EActionOp.HU == info.op) {
                            info.op = EActionOp.PASS;
                        }
                    }
                    return true;
                }
            } else if (EActionOp.BAR.equals(op)) {
                for (int j = i + 1; j < len; ++j) {
                    info = this.allWait.get(j);
                    if (EActionOp.BAR == info.op) {
                        info.op = EActionOp.PASS;
                    }
                }
                return true;
            } else if (EActionOp.BUMP.equals(op)) {
                for (int j = i + 1; j < len; ++j) {
                    info = this.allWait.get(j);
                    if (EActionOp.BUMP == info.op) {
                        info.op = EActionOp.PASS;
                    }
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean action(boolean timeout) {
        WaitInfo wait1 = this.allWait.get(0);
        if (EActionOp.PASS == wait1.getOp()) {
            ((IMahjongRoom) this.room).onPass();
        } else if (EActionOp.BAR == wait1.getOp()) {
            ((IMahjongRoom) this.room).onBar(this.takePlayer, (IMahjongPlayer) this.room.getRoomPlayer(wait1.getPlayerUid()), wait1.getParam());
        } else if (EActionOp.BUMP == wait1.getOp()) {
            ((IMahjongRoom) this.room).onBump(this.takePlayer, (IMahjongPlayer) this.room.getRoomPlayer(wait1.getPlayerUid()), wait1.getParam());
        } else if (EActionOp.EAT == wait1.getOp()) {
            ((IMahjongRoom) this.room).onEat(this.takePlayer, (IMahjongPlayer) this.room.getRoomPlayer(wait1.getPlayerUid()), wait1.getParam());
        } else if (EActionOp.HU == wait1.getOp()) {
            int huCnt = 0;
            for (WaitInfo info : this.allWait) {
                if (EActionOp.HU == info.getOp()) {
                    ++huCnt;
                } else {
                    break;
                }
            }
            if (1 == huCnt) {
                ((IMahjongRoom) this.room).onHu(this.takePlayer
                        , (IMahjongPlayer) this.room.getRoomPlayer(wait1.getPlayerUid())
                        , this.takeCard);
            } else if (2 == huCnt) {
                ((IMahjongRoom) this.room).onHu(this.takePlayer
                        , (IMahjongPlayer) this.room.getRoomPlayer(wait1.getPlayerUid())
                        , (IMahjongPlayer) this.room.getRoomPlayer(this.allWait.get(1).getPlayerUid())
                        , this.takeCard);
            } else if (3 == huCnt) {
                ((IMahjongRoom) this.room).onHu(this.takePlayer
                        , (IMahjongPlayer) this.room.getRoomPlayer(wait1.getPlayerUid())
                        , (IMahjongPlayer) this.room.getRoomPlayer(this.allWait.get(1).getPlayerUid())
                        , (IMahjongPlayer) this.room.getRoomPlayer(this.allWait.get(2).getPlayerUid())
                        , this.takeCard);
            }
        }
        return true;
    }

    @Override
    public boolean canAction(long curTime) {
        if (!this.active) {
            return false;
        }
        boolean flag = false;
        for (WaitInfo info : this.allWait) {
            if (EActionOp.NORMAL == info.op) {
                if (-1 == info.timeout && curTime - this.startTime + this.useTime < Constant.ROOM_TAKE_TIMEOUT) {
                    continue;
                }
                if ((curTime - this.startTime + this.useTime) < info.getTimeout()) {
                    continue;
                }

                IMahjongPlayer player = (IMahjongPlayer) this.room.getRoomPlayer(info.getIndex());
                player.operationTimeout();
                if (info.isHu() && info.isCloseYHBH()) {
                    this.opWait(info, EActionOp.PASS);
                } else {
                    this.opWait(info, info.isHu() ? EActionOp.HU : EActionOp.PASS);
                }
                flag = true;
            }
        }
        if (flag) {
            return this.check();
        }
        return false;
    }

    @Override
    protected void doRecover() {
        for (WaitInfo info : this.allWait) {
            if (EActionOp.NORMAL != info.getOp()) {
                continue;
            }
            IMahjongPlayer player = (IMahjongPlayer) this.room.getRoomPlayer(info.getPlayerUid());
            ((IMahjongRoom) this.room).doSendCanOperate(player, info.hu, info.bar, info.bump, info.eat, this.takeCard);
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        WaitInfo info = this.getWaitInfo(player.getUid());
        if (null == info|| EActionOp.NORMAL != info.op) {
            return;
        }

        ((IMahjongRoom) this.room).doSendCanOperate((IMahjongPlayer) player, info.hu, info.bar, info.bump, info.eat, this.takeCard);
    }

    public byte getTakeCard() {
        return takeCard;
    }

    public void setTakeCard(byte takeCard) {
        this.takeCard = takeCard;
    }
}
