package com.xiuxiu.app.server.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.xiuxiu.app.server.notice.Notice;

public interface INoticeMapper {
	
	/**
	 * column为数据库字段名
	 * porperty为实体类属性名
	 * @return
	 */
	@Select("SELECT * FROM `notice`")
	@Results({ @Result(property = "type", column = "type"),@Result(property = "content", column = "context") })
    List<Notice> loadAll();
	
}
