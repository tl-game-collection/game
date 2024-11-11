package com.xiuxiu.app.server.db.dao;

import java.util.List;

import com.xiuxiu.app.server.club.ClubInfo;

public interface IClubInfoDAO extends IBaseDAO<ClubInfo>{

    /**
     * 根据id查找亲友圈信息
     * 
     * @param clubInfoUid
     * @return
     */
    ClubInfo loadByUid(long clubInfoUid);

    /**
     * 加载所有亲友圈信息
     * 
     * @return
     */
    List<ClubInfo> loadAll();

    /**
     * 分页获取所有亲友圈id列表
     * 
     * @param limit
     * @param offset
     * @return
     */
    List<Long> loadByPage(long limit, long offset);

    /**
     * 判断亲友圈名称是否已存在
     * 
     * @param clubType
     * @param name
     * @return
     */
    int isExistName(int clubType, String name);

    /**
     * 通过圈主id加载所有亲友圈信息
     * @param ownerId
     * @return
     */
    List<ClubInfo> loadByOwnerId(long ownerId);
}
