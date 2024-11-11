package com.xiuxiu.app.server.chat;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.chat.PCLIChatNtfLastMsgUidInfo;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.ds.ConcurrentHashSet;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MailBoxUid extends BaseTable {
    protected AtomicLong lastMsgUid = new AtomicLong(0);
    protected long lastMsgUidByClient = 0;
    protected ConcurrentHashSet<Long> recallMsgUid = new ConcurrentHashSet<>();
    protected transient AtomicLong lastUpdateTime = new AtomicLong(-1);
    protected transient AtomicLong opaque = new AtomicLong(0);
    // TODO remove timeouts in the timer
    protected transient ConcurrentHashMap<Long, LoadMailBoxParam> allClientOperator = new ConcurrentHashMap<>(16);

    public MailBoxUid() {
        this.tableType = ETableType.TB_MAILBOX_UID;
    }

    public LoadMailBoxParam getMailBoxOperator(long lastMsgUid) {
        LoadMailBoxParam param = new LoadMailBoxParam();
        param.setOpaque(this.opaque.incrementAndGet());
        param.setClientLastMsgUid(this.lastMsgUidByClient);
        if (lastMsgUid > this.lastMsgUid.get()) {
            lastMsgUid = this.lastMsgUid.get();
        }
        param.setLastMsgUid(lastMsgUid);
        Iterator<Long> it = this.recallMsgUid.iterator();
        while (it.hasNext()) {
            long recallUid = it.next();
            if (recallUid < lastMsgUid) {
                param.getRecallMsgUid().add(recallUid);
            }
        }
        this.allClientOperator.putIfAbsent(param.getOpaque(), param);
        return param;
    }

    public void mailBoxAckOk(long opaque) {
        LoadMailBoxParam param = this.allClientOperator.remove(opaque);
        if (null == param) {
            return;
        }
        if (this.lastMsgUidByClient != param.getClientLastMsgUid()) {
            return;
        }
        this.lastMsgUidByClient = param.getLastMsgUid();
        Iterator<Long> it = param.getRecallMsgUid().iterator();
        while (it.hasNext()) {
            this.recallMsgUid.remove(it.next());
        }
        this.lastUpdateTime.incrementAndGet();
    }

    public void sendLoadMailBox(Player player) {
        if (this.lastMsgUidByClient >= this.lastMsgUid.get()) {
            return;
        }
        PCLIChatNtfLastMsgUidInfo chatNtfLastMsgUidInfo = new PCLIChatNtfLastMsgUidInfo();
//        long loadLastMsgUid = this.lastMsgUid.get() > (this.lastMsgUidByClient + 30) ? (this.lastMsgUidByClient + 30) : this.lastMsgUid.get();
        chatNtfLastMsgUidInfo.lastMsgUid = Math.min(this.lastMsgUid.get(), (this.lastMsgUidByClient + 30));
        player.send(CommandId.CLI_NTF_CHAT_LAST_MSG_ID, chatNtfLastMsgUidInfo);
    }

    public void recallMessageUid(long messageUid) {
        this.recallMsgUid.add(messageUid);
        this.lastUpdateTime.incrementAndGet();
    }

    public void updateMessageAckOk(long messageUid) {
        this.recallMsgUid.remove(messageUid);
        this.lastUpdateTime.incrementAndGet();
    }

    public long lastMsgUidInc() {
        this.lastUpdateTime.incrementAndGet();
        return this.lastMsgUid.incrementAndGet();
    }

    public long getLastMsgUid() {
        return lastMsgUid.get();
    }

    public void setLastMsgUid(long lastMsgUid) {
        this.lastMsgUid.set(lastMsgUid);
    }

    public long getLastMsgUidByClient() {
        return lastMsgUidByClient;
    }

    public void setLastMsgUidByClient(long lastMsgUidByClient) {
        this.lastMsgUidByClient = lastMsgUidByClient;
        this.lastUpdateTime.incrementAndGet();
    }

    public ConcurrentHashSet<Long> getRecallMsgUid() {
        return recallMsgUid;
    }

    public void setRecallMsgUid(ConcurrentHashSet<Long> recallMsgUid) {
        this.recallMsgUid = recallMsgUid;
    }

    public String getRecallMsgUidDb() {
        return JsonUtil.toJson(this.recallMsgUid);
    }

    public void setRecallMsgUidDb(String value) {
        if (StringUtil.isEmptyOrNull(value)) {
            return;
        }
        this.recallMsgUid = JsonUtil.fromJson(value, new TypeReference<ConcurrentHashSet<Long>>() {});
    }

    public AtomicLong getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(AtomicLong lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "MailBoxUid{" +
                "lastMsgUid=" + lastMsgUid +
                ", lastMsgUidByClient=" + lastMsgUidByClient +
                ", recallMsgUid=" + recallMsgUid +
                ", lastUpdateTime=" + lastUpdateTime +
                ", opaque=" + opaque +
                ", allClientOperator=" + allClientOperator +
                ", isNew=" + isNew +
                ", tableType=" + tableType +
                ", uid=" + uid +
                ", dirty=" + dirty +
                '}';
    }
}
