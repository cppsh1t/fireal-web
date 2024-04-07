package com.fireal.web.objectPool;

public interface ObjectPool<T> {

    T release();

    void recycle(T obj);

} 
