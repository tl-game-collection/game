package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.player.Nickname;

public interface INicknameDAO extends IBaseDAO<Nickname> {
    public Nickname getOne();
}
