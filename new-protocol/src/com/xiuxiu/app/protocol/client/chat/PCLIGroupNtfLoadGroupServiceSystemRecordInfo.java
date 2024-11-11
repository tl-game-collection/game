package com.xiuxiu.app.protocol.client.chat;

import java.util.ArrayList;
import java.util.List;

/** 
* @date 创建时间：2019年7月9日 上午12:05:21 *
* User:wk
**/
public class PCLIGroupNtfLoadGroupServiceSystemRecordInfo {
	   public static class GroupServiceSystemRecordInfo {
	        public long playerUid;          // 玩家uid
	        public String playerName;       // 玩家name
	        public long joinTime;           // 时间(ms)
	        public long groupUid;           // 群uid

	        @Override
	        public String toString() {
	            return "GroupServiceSystemRecordInfo{" +
	                    "playerUid=" + playerUid +
	                    ", playerName=" + playerName +
	                    ", groupUid=" + groupUid +
	                    ", joinTime=" + joinTime +
	                    '}';
	        }
	    }

	    public List<GroupServiceSystemRecordInfo> data = new ArrayList<>(); // 数据

	    @Override
	    public String toString() {
	        return "PCLIGroupNtfLoadGroupServiceSystemRecordInfo{" +
	                "data=" + data +
	                '}';
	    }
}
