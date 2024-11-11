package com.xiuxiu.app.protocol.client.player;

// 请求编辑提现方式
public class PCLIPlayerReqEditWalletWithdrawType {
    public int id;                      // 提现方式Uid
    public int type;                    // 提现类型 1:微信 2:支付宝  3:银行卡
    public String payeeAccount;         // 收款方账号
    public String payeeRealName;        // 收款方真实姓名
    public String payeeAddress;         // 收款方开户行
    public String payeeLocation;        // 收款方开户行地址

    @Override
    public String toString() {
        return "PCLIPlayerReqEditWalletWithdrawType{" +
                "id=" + id +
                ", type=" + type +
                ", payeeAccount='" + payeeAccount + '\'' +
                ", payeeRealName='" + payeeRealName + '\'' +
                ", payeeAddress='" + payeeAddress + '\'' +
                ", payeeLocation='" + payeeLocation + '\'' +
                '}';
    }
}
