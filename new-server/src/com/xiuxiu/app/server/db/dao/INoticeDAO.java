package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.notice.Notice;

import java.util.List;

public interface INoticeDAO extends IBaseDAO<Notice> {
	
    List<Notice> loadAll();

}
