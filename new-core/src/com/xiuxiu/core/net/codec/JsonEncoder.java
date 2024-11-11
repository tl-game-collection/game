package com.xiuxiu.core.net.codec;

import com.xiuxiu.core.net.protocol.ErrorCode;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

public class JsonEncoder implements Encoder {
    @Override
    public byte[] encode(Object msg) throws Exception {
        if (null == msg) {
            return null;
        }
        if (msg instanceof Throwable) {
            String errMsg = StringUtil.exception2String((Throwable) msg);
            msg = new ErrorMsg();
            ((ErrorMsg) msg).msg = errMsg;
            ((ErrorMsg) msg).ret = ErrorCode.SERVER_INTERNAL_ERROR.getRet();
        }
        return JsonUtil.toJson(msg).getBytes(Charsetutil.UTF8);
    }
}
