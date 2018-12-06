package org.jsirenia.util.runtime;

public class RuntimeUtil {
	/**
	 * @return
	 */
    public static StackTraceElement[] stackTraceElements() {
    	Exception e = new Exception();
    	return e.getStackTrace();
    }
}
