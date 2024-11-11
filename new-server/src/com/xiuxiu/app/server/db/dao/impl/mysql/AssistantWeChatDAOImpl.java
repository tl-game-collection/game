package com.xiuxiu.app.server.db.dao.impl.mysql;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.dao.IAssistantWeChatDAO;
import com.xiuxiu.app.server.db.dao.IAssistantWeChatMapper;
import com.xiuxiu.app.server.system.AssistantWeChat;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class AssistantWeChatDAOImpl implements IAssistantWeChatDAO {
    private final SqlSessionFactory factory;

    public AssistantWeChatDAOImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean save(AssistantWeChat info) {
        try (SqlSession session = this.factory.openSession(true)) {
            IAssistantWeChatMapper mapper = session.getMapper(IAssistantWeChatMapper.class);
            if (1 == mapper.save(info)) {
                return true;
            }
            if (1 == mapper.create(info)) {
                return true;
            }
        } catch (Exception e) {
            Logs.DB.error("保存微信客服失败 Info:%s", e, info);
        } finally {

        }
        return false;
    }

    @Override
    public AssistantWeChat load(long uid) {
        try (SqlSession session = this.factory.openSession(true)) {
            IAssistantWeChatMapper mapper = session.getMapper(IAssistantWeChatMapper.class);
            return mapper.load(uid);
        } catch (Exception e) {
            Logs.DB.error("获取客服微信号失败 uid:%d", e, uid);
        } finally {

        }
        return null;
    }

    @Override
    public AssistantWeChat loadByProvince(String province) {
        try (SqlSession session = this.factory.openSession(true)) {
            IAssistantWeChatMapper mapper = session.getMapper(IAssistantWeChatMapper.class);
            return mapper.loadByProvince(province);
        } catch (Exception e) {
            Logs.DB.error("根据省份获取客服微信号失败 province:%s", e, province);
        } finally {

        }
        return null;
    }

    @Override
    public AssistantWeChat loadByCity(String city) {
        try (SqlSession session = this.factory.openSession(true)) {
            IAssistantWeChatMapper mapper = session.getMapper(IAssistantWeChatMapper.class);
            return mapper.loadByCity(city);
        } catch (Exception e) {
            Logs.DB.error("根据城市获取客服微信号失败 city:%s", e, city);
        } finally {

        }
        return null;
    }

    @Override
    public AssistantWeChat loadByAdCode(long adCode) {
        try (SqlSession session = this.factory.openSession(true)) {
            IAssistantWeChatMapper mapper = session.getMapper(IAssistantWeChatMapper.class);
            return mapper.loadByAdCode(adCode);
        } catch (Exception e) {
            Logs.DB.error("根据城市获取客服微信号失败 adCode:%d", e, adCode);
        } finally {

        }
        return null;
    }

    @Override
    public List<AssistantWeChat> loadAll() {
        try (SqlSession session = this.factory.openSession(true)) {
            IAssistantWeChatMapper mapper = session.getMapper(IAssistantWeChatMapper.class);
            List<AssistantWeChat> list = mapper.loadAll();
            return null == list ? Collections.EMPTY_LIST : list;
        } catch (Exception e) {
            Logs.DB.error("客服微信号列表失败", e);
        } finally {

        }
        return null;
    }

    @Override
    public boolean remove(long adCode) {
        try (SqlSession session = this.factory.openSession(true)) {
            IAssistantWeChatMapper mapper = session.getMapper(IAssistantWeChatMapper.class);
            return 1 == mapper.remove(adCode);
        } catch (Exception e) {
            Logs.DB.error("删除客服微信号失败", e);
        } finally {

        }
        return false;
    }
}
