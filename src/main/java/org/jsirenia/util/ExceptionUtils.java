package org.jsirenia.util;

import org.jsirenia.exception.ServiceException;

/**
 */
public abstract class ExceptionUtils {
    private static final String serviceExClassName = ServiceException.class.getName();

    public static <T> T throwServiceEx(String msg) {
        throw new ServiceException("-1",msg);
    }

    public static <T> T throwServiceEx(String code, String msg) {
        throw new ServiceException(code, msg);
    }

    public static String findLocation(Throwable e) {
        StackTraceElement[] traces = e.getStackTrace();
        StackTraceElement trace = traces[0];
        String className = trace.getClassName();
        String methodName = trace.getMethodName();
        String fileName = trace.getFileName();
        int lineNumber = trace.getLineNumber();
        return "at " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")";
    }

    public static void wrapWithRuntimeEx(Callback.Callback00e cb) {
        try {
            cb.apply();
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T wrapWithRuntimeEx(Callback.Callback01e<T> cb) {
        try {
            return cb.apply();
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static ServiceException unwrap(Exception e){
        Throwable cause = e;
        while(cause!=null && !(cause instanceof ServiceException)){
            cause = cause.getCause();
        }
        return (ServiceException) cause;
    }
}
