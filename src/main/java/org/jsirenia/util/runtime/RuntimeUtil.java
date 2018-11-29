package org.jsirenia.util.runtime;

public class RuntimeUtil {
	/**
	 * 获取当前执行代码所在的类和方法
	 * 注意：必须直接调用该方法，否则结果会不正确！！！
	 * @return
	 */
    public static StackTraceElement currentStackTraceElement() {
    	Exception e = new Exception();
    	StackTraceElement ele = e.getStackTrace()[1];
    	return ele;
    }
    public static void main(String[] args) {
    	StackTraceElement ele = currentStackTraceElement();
    	System.out.println(ele.getClassName());
    	System.out.println(ele.getMethodName());
	}
}
