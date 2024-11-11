package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IFloorDAO;
import com.xiuxiu.app.server.db.dao.IFloorMapper;
import com.xiuxiu.app.server.floor.Floor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class FloorDAOImpl implements IFloorDAO {
    private final SqlSessionFactory factory;

    public FloorDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }
	@Override
	public Floor load(long uid) {
		return null;
	}

	@Override
	public boolean save(Floor value) {
		try (SqlSession session = this.factory.openSession(true)) {
			IFloorMapper mapper = session.getMapper(IFloorMapper.class);
			if (1 != mapper.update(value)) {
				return 1 == mapper.create(value);
			}
				return true;
		} catch (Exception e) {
			Logs.DB.error("创建楼层信息失败 roomScore:%s", e, value);
		} finally {

		}
		return false;
	}


	@Override
	public List<Floor> loadAll() {
		try (SqlSession session = this.factory.openSession(true)) {
			IFloorMapper mapper = session.getMapper(IFloorMapper.class);
			return  mapper.loadAll();
		} catch (Exception e) {
			Logs.DB.error("加载楼层信息失败", e);
		} finally {

		}
		return null;
	}

	@Override
	public boolean deleteFloorByUid(long uid) {
		try (SqlSession session = this.factory.openSession(true)) {
			IFloorMapper mapper = session.getMapper(IFloorMapper.class);
			return  1==mapper.deleteFloorByUid(uid);
		} catch (Exception e) {
			Logs.DB.error("加载楼层信息失败", e);
		} finally {

		}
		return false;
	}
}
