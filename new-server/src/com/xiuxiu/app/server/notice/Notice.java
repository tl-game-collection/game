package com.xiuxiu.app.server.notice;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;

/**
 * 大厅公告实体类
 * 
 * @author Administrator
 *
 */
public class Notice extends BaseTable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    /** 0 更新公告， 1 系统公告 */
    private int type;
    /** 内容 */
    private String content;
    
    public Notice() {
        this.setTableType(ETableType.TB_NOTICE);
    }

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
    public String toString() {
        return "Notice{" +
                "type='" + type + '\'' +
                ", content=" + content +
                '}';
    }
    
}
