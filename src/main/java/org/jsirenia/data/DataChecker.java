package org.jsirenia.data;


import org.jsirenia.exception.ServiceException;

/**
 * 参数校验工具
 * 类似于spring的Assert,但是又不一样。spring的Assert抛出的是参数非法异常，并且没有错误编码。
 * 还有一种情况，直接执行代码，有异常则认为参数非法。
 * 
 * 只有真正需要区分错误码的时候，才指定错误码。否则不需要指定错误码。
 * 如果不加区分的，所有地方都指定错误码，那么实际上会增加很多思维负担。
 * 现实中需要错误码的地方不多。
 *
 */
public abstract class DataChecker {
    /**
     * 如果出异常，则抛业务异常
     * @param errMsg
     * @param acquirer
     */
    public static <T> T get(Acquirer<T> acquirer,String errMsg){
        T value = null;
        Exception ex = null;
        try{
            value = acquirer.get();
        }catch(Exception e){
            ex = e;
        }
        if(ex != null){
            throw new ServiceException(errMsg);
        }
        return value;
    }
    public static <T> T get(Acquirer<T> acquirer,String errMsg,String errCode){
        T value = null;
        Exception ex = null;
        try{
            value = acquirer.get();
        }catch(Exception e){
            ex = e;
        }
        if(ex != null){
            throw new ServiceException(errCode, errMsg);
        }
        return value;
    }
    public static void check(boolean exp,String errMsg,String errCode){
    	if(!exp){
            throw new ServiceException(errCode,errMsg);
        }
    }
    public static void check(boolean exp,String errMsg){
        if(!exp){
            throw new ServiceException(errMsg);
        }
    }
    public static void check(Checker checker,String errMsg,String errCode){
        boolean pass = false;
        Exception ex = null;
        try{
            pass = checker.check();
        }catch(Exception e){
            ex = e;
        }
        if(ex != null || !pass){
            throw new ServiceException(errCode,errMsg);
        }
    }
    public static void check(Checker checker,String errMsg){
        boolean pass = false;
        Exception ex = null;
        try{
            pass = checker.check();
        }catch(Exception e){
            ex = e;
        }
        if(ex != null || !pass){
            throw new ServiceException(errMsg);
        }
    }
    public interface Checker{
        boolean check() throws Exception;
    }
    public interface Acquirer<T>{
        T get() throws Exception;
    }
}
