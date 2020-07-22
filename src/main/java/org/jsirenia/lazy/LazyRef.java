package com.sfpay.msfs.jyd.common.util;

public class LazyRef<T> {
    private T value;
    private volatile boolean initialized;
    private Callbacks.Callback01<T> initializer;

    public LazyRef(Callbacks.Callback01<T> initializer){
        if(initializer==null){
            throw new RuntimeException("LazyRef构造器的initializer不能为空");
        }
        this.initializer = initializer;
    }
    public T get(){
        if(!initialized){
            synchronized(this){
                if(!initialized){
                    value = initializer.apply();
                    if(value==null){
                        throw new RuntimeException("LazyRef的initializer返回值不能为空");
                    }
                    initialized = true;
                }
            }
        }
        return value;
    }
}
