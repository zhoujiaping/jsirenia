package org.jsirenia.util.callback;
/**
 * 3个入参，没有返回值
 */
@FunctionalInterface
public interface Callback30<T1,T2,T3>{
	public void apply(T1 t1,T2 t2,T3 t3);
}
