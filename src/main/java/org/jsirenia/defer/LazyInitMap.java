package org.jsirenia.defer;

import java.util.Map;
/***
 * 第一次get的时候，调用初始化方法。
 * @author zhoujiaping  2019-04-23
 *
 */
public class LazyInitMap<K,V>{
	private  Map<K,V> map;
	private boolean initialized;
	private Initializer<K,V> initializer;
	public interface Initializer<K,V>{
		Map<K,V> init();
	}
	public LazyInitMap(Initializer<K,V> initializer){
		if(initializer==null){
			throw new RuntimeException("LazyMap构造器的initializer不能为空");
		}
		this.initializer = initializer;
	}
	public V get(K k){
		if(!initialized){
			synchronized(this){
				if(!initialized){
					map = initializer.init();
					if(map==null){
						throw new RuntimeException("LazyMap的initializer返回值不能为空");
					}
					initialized = true;
				}
			}
		}
		return map.get(k);
	}
}
