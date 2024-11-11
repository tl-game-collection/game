package com.xiuxiu.core.queue;

/**
 * 
 * <pre>
 * 所有使用 FixedAsynchronousQueue 的 对象 T都要实现这个接口  
 * 用来标记当前T 在 队列中的索引
 * </pre>
 */
public interface SupportAsynchronous {

    public int getIndex();

    public void setIndex(int index);

    public void clearIndex();
}
