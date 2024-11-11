package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.db.dao.IBoxDAO;
import com.xiuxiu.app.server.db.dao.IBoxMapper;
import com.xiuxiu.app.server.db.dao.IClubMemberMapper;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class BoxDAOImpl implements IBoxDAO {
    private final SqlSessionFactory factory;

    public BoxDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }
	@Override
	public Box load(long uid) {
		return null;
	}

	@Override
	public boolean save(Box value) {
		try (SqlSession session = this.factory.openSession(true)) {
			IBoxMapper mapper = session.getMapper(IBoxMapper.class);
			if (1 != mapper.save(value)) {
                return 1 == mapper.create(value);
            }
		} catch (Exception e) {
			Logs.DB.error("创建包厢信息失败 roomScore:%s", e, value);
		} finally {

		}
		return false;
	}

	@Override
	public List<Box> loadAll() {
		try (SqlSession session = this.factory.openSession(true)) {
			IBoxMapper mapper = session.getMapper(IBoxMapper.class);
			return  mapper.loadAll();
		} catch (Exception e) {
			Logs.DB.error("加载包厢信息失败", e);
		} finally {

		}
		return null;
	}
	
    @Override
    public void delete(long uid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IBoxMapper mapper = session.getMapper(IBoxMapper.class);
            mapper.delete(uid);
        } catch (Exception e) {
            Logs.DB.error("删除包厢", e);
        } finally {

        }
    }

}
