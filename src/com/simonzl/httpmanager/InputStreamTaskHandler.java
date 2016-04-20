package com.simonzl.httpmanager;

import java.io.InputStream;
/**
 * TaskHandler<T> 子类，泛型T指定为InputStream
 * @author SimonZl
 *
 */
public abstract class InputStreamTaskHandler extends TaskHandler<InputStream>{

	@Override
	public InputStream parseResult(InputStream result) {
		// TODO Auto-generated method stub
		return result;
	}
}
