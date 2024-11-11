package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.INoticeDAO;
import com.xiuxiu.app.server.db.dao.INoticeMapper;
import com.xiuxiu.app.server.notice.Notice;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class NoticeDAOImpl implements INoticeDAO {
    private final SqlSessionFactory factory;

    public NoticeDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

	@Override
	public List<Notice> loadAll() {
		try (SqlSession session = this.factory.openSession(true)) {
			INoticeMapper mapper = session.getMapper(INoticeMapper.class);
			return  mapper.loadAll();
		} catch (Exception e) {
			Logs.DB.error("加载包厢信息失败", e);
		} finally {

		}
		return null;
	}

	@Override
	public Notice load(long uid) {
		return null;
	}

	@Override
	public boolean save(Notice value) {
		return false;
	}

}
