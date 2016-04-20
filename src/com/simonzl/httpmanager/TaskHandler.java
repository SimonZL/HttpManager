package com.simonzl.httpmanager;

import java.io.InputStream;
/**
 * 网络请求完成抽象类
 * @author SimonZl
 *
 * @param <T>
 */
public abstract class TaskHandler<T> {
	/**请求成功 */
    public abstract void onSuccess(T result);  
    /**请求失败 */
    public abstract void onFail();  
    /**转换InputStream类型返回值成泛型T */
    public abstract T parseResult(InputStream result);  
}
