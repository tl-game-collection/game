package com.xiuxiu.app.server.room.normal.mahjong.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfCanOperateInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong.MahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong.MahjongPlayer;
import com.xiuxiu.app.server.room.record.mahjong.MahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong.WaitSelectRecordAction;

// TODO 需要添加3人胡
public class MahjongWaitAction extends BaseMahjongAction {
    private ArrayList<WaitInfo> wait = new ArrayList<>();
    private MahjongPlayer takePlayer;
    private boolean pass = false;
    private int huType = 0;
    private long huCard = 0;
    private WaitInfo wait1 = null;
    private WaitInfo wait2 = null;
    private WaitInfo wait3 = null;

    public MahjongWaitAction(MahjongRoom room, MahjongPlayer roomPlayer, long timeout) {
        super(room, EActionOp.WAIT, roomPlayer, timeout);
    }

    public WaitInfo addWait(MahjongPlayer roomPlayer, boolean eat, boolean bump, List<Byte> bar, boolean hu) {
        WaitInfo waitInfo = new WaitInfo();
        waitInfo.roomPlayer = roomPlayer;
        waitInfo.op = EActionOp.NORMAL;
        waitInfo.eat = eat;
        waitInfo.bump = bump;
        waitInfo.bar = bar;
        waitInfo.hu = hu;
        if (this.wait.isEmpty() || waitInfo.hu) {
            this.wait.add(0, waitInfo);
        } else {
            int i = 0, len = this.wait.size();
            for (; i < len; ++i) {
                WaitInfo temp = this.wait.get(i);
                if (temp.hu) {
                    continue;
                }
                break;
            }
            if (i == len) {
                this.wait.add(waitInfo);
            } else {
                this.wait.add(i, waitInfo);
            }
        }
        if (Switch.DEBUG && hu) {
            Logs.ROOM.debug("%s %s 胡", this.room, roomPlayer);
        }
        return waitInfo;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            this.checkTimeout(System.currentTimeMillis());
            this.check();
        }
        WaitSelectRecordAction waitSelectRecordAction = ((MahjongRecord) ((Room)this.room).getRecord()).addWaitSelectRecordAction();
        Iterator<WaitInfo> it = this.wait.iterator();
        while (it.hasNext()) {
            WaitInfo temp = it.next();
            waitSelectRecordAction.addSelect(temp.roomPlayer.getUid(), temp.op);
        }
        if (this.pass) {
            ((MahjongRoom) this.room).onPass(this.takePlayer, this.huType, this.huCard);
        } else {
            if (null == this.wait2) {
                if (EActionOp.HU == this.wait1.op) {
                    //((MahjongRoom) this.room).onQaingGangHu(this.takePlayer, this.wait1.roomPlayer, null, null, this.wait1.cardValue);
                    ((MahjongRoom) this.room).onHu(this.takePlayer, this.wait1.roomPlayer, null, null, this.wait1.cardValue, this.wait1.huType);
                } else if (EActionOp.BAR == this.wait1.op) {
                    ((MahjongRoom) this.room).onBar(this.takePlayer, this.wait1.roomPlayer, this.wait1.cardValue, this.wait1.index, this.wait1.endIndex, this.wait1.insertIndex, this.wait1.bar);
                } else if (EActionOp.BUMP == this.wait1.op) {
                    ((MahjongRoom) this.room).onBump(this.takePlayer, this.wait1.roomPlayer, this.wait1.cardValue, this.wait1.index);
                }
            } else {
                if (this.wait1.op == this.wait2.op) {
                    //((MahjongRoom) this.room).onQaingGangHu(this.takePlayer, this.wait1.roomPlayer, this.wait2.roomPlayer, null, this.wait1.cardValue);
                    ((MahjongRoom) this.room).onHu(this.takePlayer, this.wait1.roomPlayer, this.wait2.roomPlayer, null, this.wait1.cardValue, this.wait2.huType);
                } else {
                    WaitInfo wait3 = this.wait1.op.ordinal() > this.wait2.op.ordinal() ? this.wait1 : this.wait2;
                    if (EActionOp.HU == wait3.op) {
                        //((MahjongRoom) this.room).onQaingGangHu(this.takePlayer, wait3.roomPlayer, null, null, this.wait1.cardValue);
                        ((MahjongRoom) this.room).onHu(this.takePlayer, wait3.roomPlayer, null, null, wait3.cardValue, wait3.huType);
                    } else if (EActionOp.BAR == wait3.op) {
                        ((MahjongRoom) this.room).onBar(this.takePlayer, wait3.roomPlayer, wait3.cardValue, wait3.index, wait3.endIndex, wait3.insertIndex, wait3.bar);
                    } else if (EActionOp.BUMP == wait3.op) {
                        ((MahjongRoom) this.room).onBump(this.takePlayer, wait3.roomPlayer, wait3.cardValue, wait3.index);
                    }
                }
            }
        }
        return true;
    }

    public boolean check() {
        this.wait1 = null;
        this.wait2 = null;
        this.wait3 = null;

        int huCnt = 0;
        boolean hasHu = false;
        boolean hasNormal = false;
        this.huType = 0;
        this.huCard = 0;

        for (int i = 0, len = this.wait.size(); i < len; ++i) {
            WaitInfo temp = this.wait.get(i);
            if (EActionOp.PASS == temp.op) {
                if (temp.hu) {
                    this.huType |= (1 << temp.huType);
                    this.huCard |= (temp.cardValue << (6 * temp.huType));
                }
                continue;
            }
            if (temp.hu) {
                ++huCnt;
                hasHu = true;
                this.huType |= (1 << temp.huType);
                this.huCard |= (temp.cardValue << (6 * temp.huType));
            }
            if (EActionOp.NORMAL == temp.op) {
                hasNormal = true;
            } else {
                if (temp.hu) {
                    --huCnt;
                }
                if (null == this.wait1) {
                    this.wait1 = temp;
                } else if (null == this.wait2) {
                    this.wait2 = temp;
                } else if (null == this.wait3) {
                    this.wait3 = temp;
                }
            }
        }

        if (hasHu) {
            if (huCnt > 0) {
                Logs.ROOM.debug("%s 有人胡, 还没有选择", this.room);
                return false;
            }
            return true;
        }
        if (hasNormal) {
            Logs.ROOM.debug("%s 还有人没有选择", this.room);
            return false;
        }
        if (null == this.wait3 && null == this.wait2 && null == this.wait1) {
            Logs.ROOM.debug("%s 所有人都Pass", this.room);
            this.pass = true;
        }
        return true;
    }

    public WaitInfo getWaitInfo(long playerUid) {
        Iterator<WaitInfo> it = this.wait.iterator();
        while (it.hasNext()) {
            WaitInfo temp = it.next();
            if (temp.roomPlayer.getUid() == playerUid) {
                return temp;
            }
        }
        return null;
    }

    public void setTakePlayer(MahjongPlayer takePlayer) {
        this.takePlayer = takePlayer;
    }

    @Override
    public boolean canAction(long curTime) {
        if (!this.active) {
            return false;
        }
        return this.checkTimeout(curTime);
    }

    protected boolean checkTimeout(long curTime) {
        int selectHuCnt = 0;
        boolean hasOther = false;
        boolean hasHu = false;
        Iterator<WaitInfo> it = this.wait.iterator();
        while (it.hasNext()) {
            WaitInfo temp = it.next();
            if (EActionOp.NORMAL != temp.op) {
                if (EActionOp.HU == temp.op) {
                    ++selectHuCnt;
                }
                continue;
            }
            if (-1 == temp.timeout || (curTime - this.startTime + this.useTime) < temp.timeout) {
                if (temp.hu) {
                    hasHu = true;
                    break;
                }
                hasOther = true;
                continue;
            }
            if (temp.roomPlayer.isBright()) {
                if (temp.hu) {
                    temp.op = EActionOp.HU;
                    ++selectHuCnt;
                } else if (null != temp.bar) {
                    temp.op = EActionOp.BAR;
                }
            } else {
                if (temp.hu) {
                    temp.op = EActionOp.HU;
                    ++selectHuCnt;
                } else {
                    temp.op = EActionOp.PASS;
                }
            }
        }
        if (hasHu) {
            return false;
        }
        if (selectHuCnt > 0 && hasOther) {
            it = this.wait.iterator();
            while (it.hasNext()) {
                WaitInfo temp = it.next();
                if (EActionOp.NORMAL != temp.op) {
                    continue;
                }
                temp.roomPlayer.operationTimeout();
                temp.op = EActionOp.PASS;
            }
            return true;
        }
        return !hasOther;
    }

    @Override
    protected void doRecover() {
        Iterator<WaitInfo> it = this.wait.iterator();
        while (it.hasNext()) {
            WaitInfo waitInfo = it.next();
            if (EActionOp.NORMAL != waitInfo.op) {
                continue;
            }
            PCLIMahjongNtfCanOperateInfo canOperateInfo = new PCLIMahjongNtfCanOperateInfo(waitInfo.bump, null != waitInfo.bar, waitInfo.hu, waitInfo.eat, waitInfo.cardValue);
            waitInfo.roomPlayer.send(CommandId.CLI_NTF_MAHJONG_CAN_OPERATE, canOperateInfo);
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        WaitInfo waitInfo = this.getWaitInfo(player.getUid());
        if (null == waitInfo || EActionOp.NORMAL != waitInfo.op) {
            return;
        }

        waitInfo.timeout = waitInfo.sourceTimeout;

        PCLIMahjongNtfCanOperateInfo canOperateInfo = new PCLIMahjongNtfCanOperateInfo(waitInfo.bump, null != waitInfo.bar, waitInfo.hu, waitInfo.eat, waitInfo.cardValue);
        waitInfo.roomPlayer.send(CommandId.CLI_NTF_MAHJONG_CAN_OPERATE, canOperateInfo);
    }

    @Override
    public void offline(IRoomPlayer player) {
        WaitInfo waitInfo = this.getWaitInfo(player.getUid());
        if (null == waitInfo || EActionOp.NORMAL != waitInfo.op) {
            return;
        }
        waitInfo.timeout = 10000;
    }

    public static class WaitInfo {
        public MahjongPlayer roomPlayer;
        public EActionOp op;
        public long timeout;
        public long sourceTimeout;
        public boolean eat;
        public boolean bump;
        public List<Byte> bar;
        public boolean hu;
        public byte index;
        public byte cardValue;
        public byte endIndex;
        public byte insertIndex;
        public int fang;
        public int huType;

        public void setTimeout(long timeout) {
            if (-1 == timeout) {
                timeout = 15000;
            }
            this.timeout = timeout;
            this.sourceTimeout = timeout;
        }
    }
}
