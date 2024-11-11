package com.xiuxiu.app.server.db.dao.impl.mysql;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IForbidDAO;
import com.xiuxiu.app.server.db.dao.IForbidMapper;
import com.xiuxiu.app.server.forbid.Forbid;

public class ForbidDAOImpl implements IForbidDAO {
    private final SqlSessionFactory factory;

    public ForbidDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Forbid load(long uid) {
        return null;
    }

    @Override
    public boolean save(Forbid value) {
        try (SqlSession session = this.factory.openSession(true)) {
            IForbidMapper mapper = session.getMapper(IForbidMapper.class);
            if (1 != mapper.save(value)) {
                return 1 == mapper.create(value);
            }
            return true;
        } catch (Exception e) {
            Logs.DB.error("保存信息失败 league:%s", e, value);
        } finally {

        }
        return false;
    }

    @Override
    public List<Forbid> loadAll() {
        try (SqlSession session = this.factory.openSession(true)) {
            IForbidMapper mapper = session.getMapper(IForbidMapper.class);
            return mapper.loadAll();
        } catch (Exception e) {
            Logs.DB.error("加载所有群组信息失败", e);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean delByUid(long uid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IForbidMapper mapper = session.getMapper(IForbidMapper.class);
            return 1 == mapper.delByUid(uid);
        } catch (Exception e) {
            Logs.DB.error("删除屏蔽失败", e);
        } finally {

        }
        return false;
    }

}
