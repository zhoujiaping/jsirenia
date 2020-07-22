package org.jsirenia.lazy;

import org.jsirenia.util.Callback;

public class LazyRef<T> {
    private volatile T value;
    private Callback.Callback01<T> initializer;

    public LazyRef(Callback.Callback01<T> initializer){
        if(initializer==null){
            throw new RuntimeException("LazyRef构造器的initializer不能为空");
        }
        this.initializer = initializer;
    }
    public T get(){
        if(value==null){
            synchronized(this){
                if(value==null){
                    value = initializer.apply();
                    if(value==null){
                        throw new RuntimeException("LazyRef的initializer返回值不能为空");
                    }
                }
            }
        }
        return value;
    }
}
