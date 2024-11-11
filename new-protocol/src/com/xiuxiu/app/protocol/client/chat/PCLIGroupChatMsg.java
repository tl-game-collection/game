package com.xiuxiu.app.protocol.client.chat;
/** 
* @date 创建时间：2019年7月8日 下午11:59:03 *
* User:wk
**/
public class PCLIGroupChatMsg {
	  public long playerUid;      // 来自玩家UID
	    public String playerName;   // 来自玩家Name
	    public long groupUid;       // 来自群UID
	    public long joinTime;       // 消息时间毫秒

	    @Override
	    public String toString() {
	        return "PCLIGroupChatMsg{" +
	                "playerUid=" + playerUid +
	                ", playerName='" + playerName + '\'' +
	                ", groupUid=" + groupUid +
	                ", joinTime=" + joinTime +
	                '}';
	    }

}
