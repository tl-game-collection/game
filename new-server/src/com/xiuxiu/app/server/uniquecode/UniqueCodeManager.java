package com.xiuxiu.app.server.uniquecode;

import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.core.KeyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UniqueCodeManager {
    private static class UniqueCodeManagerHolder {
        private static UniqueCodeManager instance = new UniqueCodeManager();
    }

    public static UniqueCodeManager I = UniqueCodeManagerHolder.instance;

    /**
     * 存放未使用的群推荐码
     */
    private List<UniqueCode> groupRecommendCode = new ArrayList<>();
    /**
     * 存放群推荐码缓存 key:code value:keyValue:clubUid-playerUid
     */
    private Map<Long, KeyValue<Long,Long>> groupRecommendParamCache = new ConcurrentHashMap<>();


    public KeyValue<Long,Long> getGroupRecommendParamByCode(long code){
        KeyValue<Long,Long> result = this.groupRecommendParamCache.get(code);
        if (null == result){
            UniqueCode uniqueCode = DBManager.I.getUniqueCodeDao().loadByCodeAndType(code,EUniqueCode.GROUP_RECOMMEND_COED.getValue());
            if (null != uniqueCode){
                String[] params = uniqueCode.getParam().split(":");
                if (params.length == 2){
                    this.groupRecommendParamCache.putIfAbsent(code,new KeyValue<>(Long.valueOf(params[1]),Long.valueOf(params[0])));
                    result = this.groupRecommendParamCache.get(code);
                }
            }
        }
        return result;
    }

    public long makeGroupRecommendCode(long playerUid,long groupUid){
        UniqueCode uniqueCode = null;
        synchronized (this.groupRecommendCode) {
            int size = this.groupRecommendCode.size();
            if (size <= 0) {
                loadGroupRecommendCode();
            }
            size = this.groupRecommendCode.size();
            if (size <= 0) {
                return 0;
            }
            uniqueCode = this.groupRecommendCode.remove(size - 1);
        }
        uniqueCode.setState(0);
        uniqueCode.setParam(playerUid+":"+groupUid);
        uniqueCode.setDirty(true);
        uniqueCode.save();
        this.groupRecommendParamCache.putIfAbsent(uniqueCode.getCode(),new KeyValue<>(groupUid,playerUid));
        return uniqueCode.getCode();
    }

    private void loadGroupRecommendCode(){
        this.groupRecommendCode = DBManager.I.getUniqueCodeDao().loadUnused(EUniqueCode.GROUP_RECOMMEND_COED.getValue(),10000);
        if (null == this.groupRecommendCode){
            this.groupRecommendCode = new ArrayList<>();
        }
    }

    //public boolean checkIsCodeNotGroupUid(long groupUid){
//        return groupUid > Constant.MAX_GROUP_UID;
//    }
}
