package com.xiuxiu.core.queue;

/**
 * 提供一个SupportAsynchronous默认实现
 */
public abstract class AbstractSupportAsynchronous implements SupportAsynchronous {

    private int index = -1;	
	@Override
	public int getIndex() {
		return this.index;
	}

	@Override
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public void clearIndex() {
		index = -1;
	}

}


