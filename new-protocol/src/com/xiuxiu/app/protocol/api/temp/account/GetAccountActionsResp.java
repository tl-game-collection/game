package com.xiuxiu.app.protocol.api.temp.account;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.List;

public class GetAccountActionsResp extends ErrorMsg {
    public Data data;

    public GetAccountActionsResp(ErrorCode err) {
        super(err);
    }

    @Override
    public String toString() {
        return "GetAccountActionsResp{" +
                "ret=" + ret +
                "msg=" + msg +
                "data=" + data +
                "}";
    }

    public static class Data {
        public long totalCount; // 记录总数
        public List<AccountAction> entities; // 记录

        @Override
        public String toString() {
            return "Data{" +
                    "totalCount=" + totalCount +
                    ", entities=" + entities +
                    '}';
        }
    }

    public static class AccountAction {
        public long actionUid; // 记录ID
        public long timestamp; // 记录时间戳
        public long accountUid; // 账号ID
        public int accountType; // 账号类型，与account信息一致
        public int serverId; // 服务器ID
        public String device; // 终端设备型号
        public String deviceId; // 终端设备ID
        public String osVersion; // 终端操作系统版本
        public String address; // 地址
        public String clientVersion; // 客户端版本
        public int appId; // 客户端app ID
        public int channelId; // 渠道ID
        public String mobileNumber; // 手机号

        @Override
        public String toString() {
            return "AccountAction{" +
                    "actionUid=" + actionUid +
                    ", timestamp=" + timestamp +
                    ", accountUid=" + accountUid +
                    ", accountType=" + accountType +
                    ", serverId=" + serverId +
                    ", device='" + device + '\'' +
                    ", deviceId='" + deviceId + '\'' +
                    ", osVersion='" + osVersion + '\'' +
                    ", address='" + address + '\'' +
                    ", clientVersion='" + clientVersion + '\'' +
                    ", appId=" + appId +
                    ", channelId=" + channelId +
                    ", mobileNumber='" + mobileNumber + '\'' +
                    '}';
        }
    }
}
