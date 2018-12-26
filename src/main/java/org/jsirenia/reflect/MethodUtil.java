package org.jsirenia.reflect;

import java.lang.reflect.Method;

public class MethodUtil {
	public static Method getMethodByName(String clazz, String method) {
		try {
			Class<?> klass = Class.forName(clazz);
			Method[] methods = klass.getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().equals(method)) {
					return methods[i];
				}
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 对于无参方法，需要传空数组
	 * 
	 * @param clazz
	 * @param method
	 * @param argTypes
	 * @return
	 */
	public static Method getMethod(String clazz, String method, String... argTypes) {
		try {
			Class<?> klass = Class.forName(clazz);
			if (argTypes == null) {
				return klass.getMethod(method);
			}
			Class<?>[] types = new Class<?>[argTypes.length];
			for (int i = 0; i < types.length; i++) {
				types[i] = Class.forName(argTypes[i]);
			}
			return klass.getMethod(method, types);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
