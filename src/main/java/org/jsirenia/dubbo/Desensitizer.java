package org.jsirenia.dubbo;
/**
 * 脱敏接口
 *
 */
@FunctionalInterface
public interface Desensitizer {
	String apply(String value);
}
