package com.commitextractor.util;

import java.io.IOException;

public abstract class SingletonBase<T> {
	protected T instance = null;
	
	public T get() throws IOException {
		if (instance == null) {
			init();
		}
		
		return instance;
	}
	
	protected abstract void init() throws IOException;
}
