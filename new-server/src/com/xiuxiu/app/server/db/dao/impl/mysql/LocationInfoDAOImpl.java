package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.ILocationInfoDAO;
import com.xiuxiu.app.server.db.dao.ILocationInfoMapper;
import com.xiuxiu.app.server.system.LocationInfo;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class LocationInfoDAOImpl implements ILocationInfoDAO {
    private final SqlSessionFactory factory;

    public LocationInfoDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean save(LocationInfo info) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILocationInfoMapper mapper = session.getMapper(ILocationInfoMapper.class);
            if (1 == mapper.save(info)) {
                return true;
            }
            if (1 == mapper.create(info)) {
                return true;
            }
        } catch (Exception e) {
            Logs.DB.error("保存地理位置信息失败 Info:%s", e, info);
        } finally {

        }
        return false;
    }

    @Override
    public LocationInfo load(long uid) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILocationInfoMapper mapper = session.getMapper(ILocationInfoMapper.class);
            return mapper.load(uid);
        } catch (Exception e) {
            Logs.DB.error("获取地理位置信息失败 uid:%d", e, uid);
        } finally {

        }
        return null;
    }

    @Override
    public LocationInfo loadByLocation(String location) {
        try (SqlSession session = this.factory.openSession(true)) {
            ILocationInfoMapper mapper = session.getMapper(ILocationInfoMapper.class);
            return mapper.loadByLocation(location);
        } catch (Exception e) {
            Logs.DB.error("根据地理位置，获取地理位置信息失败 location:%s", e, location);
        } finally {

        }
        return null;
    }
}
