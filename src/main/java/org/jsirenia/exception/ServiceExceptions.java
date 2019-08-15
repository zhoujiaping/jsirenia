package org.jsirenia.exception;

/**
 */
public abstract class ServiceExceptions {
	//private static final Logger logger = LoggerFactory.getLogger(ServiceExceptions.class);

	public static String findLocation(Throwable e) {
		StackTraceElement[] traces = e.getStackTrace();
		StackTraceElement trace = traces[0];
		String className = trace.getClassName();
		String methodName = trace.getMethodName();
		String fileName = trace.getFileName();
		int lineNumber = trace.getLineNumber();
		return "at " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")";
	}
}
