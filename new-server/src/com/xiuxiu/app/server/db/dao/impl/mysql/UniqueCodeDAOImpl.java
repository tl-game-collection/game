package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IUniqueCodeDAO;
import com.xiuxiu.app.server.db.dao.IUniqueCodeMapper;
import com.xiuxiu.app.server.uniquecode.UniqueCode;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class UniqueCodeDAOImpl implements IUniqueCodeDAO {
    private final SqlSessionFactory factory;

    public UniqueCodeDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public UniqueCode load(long uid) {
        return null;
    }

    @Override
    public boolean save(UniqueCode value) {
        try (SqlSession session = this.factory.openSession(true)) {
            IUniqueCodeMapper mapper = session.getMapper(IUniqueCodeMapper.class);
            if (1 != mapper.update(value)) {
                return 1 == mapper.create(value);
            }else{
                return true;
            }
        } catch (Exception e) {
            Logs.DB.error("保存uniqueCode失败 uid:%s", e, value);
        } finally {

        }
        return false;
    }

    @Override
    public List<UniqueCode> loadUnused(int type, int cnt) {
        try (SqlSession session = this.factory.openSession(true)) {
            IUniqueCodeMapper mapper = session.getMapper(IUniqueCodeMapper.class);
            return mapper.loadUnused(type,cnt);
        } catch (Exception e) {
            Logs.DB.error("获取unique coed失败 uniqueCodeType:%d", e,type);
        } finally {

        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public UniqueCode loadByCodeAndType(long code,int type) {
        try (SqlSession session = this.factory.openSession(true)) {
            IUniqueCodeMapper mapper = session.getMapper(IUniqueCodeMapper.class);
            return mapper.loadByCodeAndType(code,type);
        } catch (Exception e) {
            Logs.DB.error("获取unique coed失败 uniqueCodeType:%d,code:%d", e,type,code);
        } finally {

        }
        return null;
    }
}
