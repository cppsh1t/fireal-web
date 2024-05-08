package io.github.cppsh1t.fireal.web.objectPool;

public interface ObjectPool<T> {

    T release();

    void recycle(T obj);

} 
