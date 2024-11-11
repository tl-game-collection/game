package com.xiuxiu.app.server.chat;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.constant.SystemTipMessageConstant;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.thread.ConsumeThread;
import com.xiuxiu.core.utils.StringUtil;
import com.xiuxiu.xpush.XPushManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MailBoxManager extends BaseManager {
	private static class MailBoxManagerHolder {
		private static MailBoxManager instance = new MailBoxManager();
	}

	public static MailBoxManager I = MailBoxManagerHolder.instance;

	private volatile boolean start = false;
	private MailBoxThread[] process = new MailBoxThread[Constant.MAX_THREAD_CNT];
	private MailBoxOpThread mailBoxOpThread = new MailBoxOpThread();
	private final ConcurrentHashMap<Long/* playerUid */, MailBoxUid/*
																	 * lastMsgUid
																	 */> allLastMsgUid = new ConcurrentHashMap<>();

	private MailBoxManager() {
	}

	public void init() {
		for (int i = 0; i < Constant.MAX_THREAD_CNT; ++i) {
			this.process[i] = new MailBoxThread(i, Constant.MAX_APPLY_CNT);
			this.process[i].start();
		}
		this.mailBoxOpThread.start();
		this.start = true;
	}

	public MailBox getMailBoxByPlayerAndMessageUid(long playerUid, long messageUid) {
		return DBManager.I.getMailBoxDao().loadByPlayerUidAndMsgUid(playerUid, messageUid);
	}

	public MailBoxUid getLastMsgUid(long playerUid) {
		MailBoxUid msgUid = this.allLastMsgUid.get(playerUid);
		if (null != msgUid) {
			return msgUid;
		}
		msgUid = DBManager.I.getMailBoxDao().getLastMsgUidByPlayerUid(playerUid);
		if (null == msgUid) {
			msgUid = new MailBoxUid();
			msgUid.setUid(playerUid);
		}
		this.allLastMsgUid.putIfAbsent(playerUid, msgUid);
		return this.allLastMsgUid.get(playerUid);
	}

	public List<MailBox> loadMailBox(Player player, LoadMailBoxParam param) {
		List<MailBox> list1 = DBManager.I.getMailBoxDao().loadByPlayerUidWithBeginUidAndEndUid(player.getUid(), param.getClientLastMsgUid(),
				param.getLastMsgUid());
		List<MailBox> list2 = DBManager.I.getMailBoxDao().loadByPlayerUidAndMsgUids(player.getUid(), param.getRecallMsgUid());
		if (list1.isEmpty()) {
			return list2;
		}
		if (!list2.isEmpty()) {
			list1.addAll(list2);
		}
		return list1;
	}

	public void updateMessageAck(long playerUid, long messageUid) {
		MailBoxUid mailBoxUid = this.getLastMsgUid(playerUid);
		mailBoxUid.updateMessageAckOk(messageUid);
	}

	public void recall(long opPlayerUid, long messageUid) {
		this.mailBoxOpThread.add(new MailBoxOpNode(opPlayerUid, messageUid, 1));
	}

	public void del(long opPlayerUid, long messageUid) {
		this.mailBoxOpThread.add(new MailBoxOpNode(opPlayerUid, messageUid, 2));
	}

	public void delRecall(long opPlayerUid, long messageUid) {
		this.mailBoxOpThread.add(new MailBoxOpNode(opPlayerUid, messageUid, 3));
	}

	public void post(MailBox mailBox) {
		if (!this.start) {
			Logs.CHAT.warn("%s 邮箱服务已经关闭, 无法投递", mailBox);
			return;
		}

		MailBoxUid lastMsgUid = MailBoxManager.I.getLastMsgUid(mailBox.toPlayerUid);
		mailBox.setMessageUidByPlayer(lastMsgUid.lastMsgUidInc());
		this.process[(int) (mailBox.getToPlayerUid() % Constant.MAX_THREAD_CNT)].add(mailBox);
	}

	@Override
	public int save() {
		int cnt = 0;
		Iterator<Map.Entry<Long, MailBoxUid>> it = this.allLastMsgUid.entrySet().iterator();
		while (it.hasNext()) {
			MailBoxUid mailBoxUid = it.next().getValue();
			final long lastUpdateTime = mailBoxUid.getLastUpdateTime().get();
			if (-1 != lastUpdateTime && DBManager.I.getMailBoxDao().saveMailBoxUid(mailBoxUid)) {
				mailBoxUid.getLastUpdateTime().compareAndSet(lastUpdateTime, -1);
				++cnt;
			}
		}
		return cnt;
	}

	@Override
	public int shutdown() {
		if (!this.start) {
			return 0;
		}
		try {
			this.start = false;
			for (int i = 0; i < Constant.MAX_THREAD_CNT; ++i) {
				this.process[i].stop();
			}
			this.mailBoxOpThread.stop();
			Iterator<Map.Entry<Long, MailBoxUid>> it = this.allLastMsgUid.entrySet().iterator();
			while (it.hasNext()) {
				MailBoxUid mailBoxUid = it.next().getValue();
				DBManager.I.getMailBoxDao().saveMailBoxUid(mailBoxUid);
			}
		} catch (Throwable e) {
		    Logs.CORE.error(e);
		}

		return 0;
	}

	private static class MailBoxOpNode {
		protected long opPlayerUid;
		protected long messageUid;
		protected int type; // 1: 撤销, 2: 删除, 3: 删除撤回

		public MailBoxOpNode(long opPlayerUid, long messageUid, int type) {
			this.opPlayerUid = opPlayerUid;
			this.messageUid = messageUid;
			this.type = type;
		}

		public long getOpPlayerUid() {
			return opPlayerUid;
		}

		public void setOpPlayerUid(long opPlayerUid) {
			this.opPlayerUid = opPlayerUid;
		}

		public long getMessageUid() {
			return messageUid;
		}

		public void setMessageUid(long messageUid) {
			this.messageUid = messageUid;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
	}

	private static class MailBoxOpThread extends ConsumeThread<MailBoxOpNode> {
		public MailBoxOpThread() {
			super("MailBoxOpThread", false);
		}

		@Override
		protected void exec(MailBoxOpNode node) {
			List<MailBox> mailBoxList = DBManager.I.getMailBoxDao().loadByMessageUid(node.getMessageUid());
			Iterator<MailBox> it = mailBoxList.iterator();
			while (it.hasNext()) {
				MailBox mailBox = it.next();
				if (1 == node.type) {
					if (0 != mailBox.getState()) {
						continue;
					}
				} else if (2 == node.type) {
					if (0 != mailBox.getState()) {
						continue;
					}
				} else if (3 == node.type) {
					if (4 != mailBox.getState()) {
						continue;
					}
				} else {
					continue;
				}
				if (1 == node.type) {
					// 撤销
					mailBox.setState((byte) 3);
					mailBox.setContentType((byte) EChatContentType.TIP.ordinal());
					if (mailBox.getFromPlayerUid() == mailBox.getToPlayerUid()) {
						// self
						mailBox.setMessage(SystemTipMessageConstant.TIP_RECALL_SELF);
					} else {
						// other
						Player fromPlayer = PlayerManager.I.getPlayer(mailBox.fromPlayerUid);
						mailBox.setMessage(String.format(SystemTipMessageConstant.TIP_RECALL_OTHER, mailBox));
					}
				} else if (2 == node.type) {
					// 删除
					mailBox.setState((byte) 4);
					if (null == mailBox.getParam()) {
						mailBox.setParam(new ArrayList<>());
					}
					Player opPlayer = PlayerManager.I.getPlayer(node.getOpPlayerUid());
					mailBox.getParam().add(null == opPlayer ? "" : opPlayer.getName());
				} else if (3 == node.type) {
					// 删除撤回
					mailBox.setState((byte) 0);
					if (null != mailBox.getParam()) {
						mailBox.getParam().remove(mailBox.getParam().size() - 1);
					}
				}

				if (DBManager.I.getMailBoxDao().save(mailBox)) {
					MailBoxUid mailBoxUid = MailBoxManager.I.getLastMsgUid(mailBox.getToPlayerUid());
					mailBoxUid.recallMessageUid(mailBox.getMessageUidByPlayer());

					Player player = PlayerManager.I.getOnlinePlayer(mailBox.getToPlayerUid());
					if (null != player) {
						player.send(CommandId.CLI_NTF_CHAT_UPDATE_MSG, mailBox.to());
					} else if (mailBox.getToPlayerUid() != mailBox.getFromPlayerUid()) {
						// TODO 暂时关闭
						String message = mailBox.getContentType() >= 0 && mailBox.getContentType() < EChatContentType.values().length
								? EChatContentType.values()[mailBox.getContentType()].getAlter()
								: null;
						if (StringUtil.isEmptyOrNull(message)) {
							message = mailBox.getMessage();
						}
//						XPushManager.I.push(String.valueOf(mailBox.getToPlayerUid()), mailBox.getFromPlayerName(), mailBox.getMessageType(),
//								message);
					}
				} else {
					this.add(node);
				}
			}
		}
	}

	private static class MailBoxThread extends ConsumeThread<MailBox> {
		public MailBoxThread(int index, int max) {
			super("MailBoxProcessThread-" + index + "/" + max, false);
		}

		@Override
		protected void exec(MailBox mailBox) {
			// 保存
			do {
				if (!DBManager.I.getMailBoxDao().save(mailBox)) {
					// save mailbox fail
					this.add(mailBox);
					Logs.CHAT.warn("%s 保存数据失败", mailBox);
					break;
				}
				Player player = PlayerManager.I.getOnlinePlayer(mailBox.getToPlayerUid());
				if (null != player) {
					MailBoxUid mailBoxUid = MailBoxManager.I.getLastMsgUid(player.getUid());
					mailBoxUid.sendLoadMailBox(player);
				} else {
					// TODO 暂时关闭
					String message = mailBox.getContentType() >= 0 && mailBox.getContentType() < EChatContentType.values().length
							? EChatContentType.values()[mailBox.getContentType()].getAlter()
							: null;
					if (StringUtil.isEmptyOrNull(message)) {
						message = mailBox.getMessage();
					}
//					XPushManager.I.push(String.valueOf(mailBox.getToPlayerUid()), mailBox.getFromPlayerName(), mailBox.getMessageType(),
//							message);
				}
			} while (false);
		}
	}
}
