package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.system.AssistantWeChat;

import java.util.List;

public interface IAssistantWeChatDAO extends IBaseDAO<AssistantWeChat> {
    @Override
    boolean save(AssistantWeChat info);
    AssistantWeChat loadByProvince(String province);
    AssistantWeChat loadByCity(String city);
    AssistantWeChat loadByAdCode(long adCode);
    List<AssistantWeChat> loadAll();
    boolean remove(long adCode);
}
