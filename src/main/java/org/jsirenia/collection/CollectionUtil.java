package org.jsirenia.collection;

import java.lang.reflect.Array;
import java.util.Collection;

import org.jsirenia.reflect.TypeUtil;

public class CollectionUtil {
	//集合转数组，或者数组转数组。支持嵌套，比如Object[][]类型转Map<String,String>[][],前提是元素的值真的是Map类型的。
	//这个可以用来解决fastjson的一个bug
	public static <T> T[] toArray(Object collection,Class<T> componentType){
		if(collection==null){
			return null;
		}
		Class<?> clazz = collection.getClass();
		if(clazz.isArray()){
			int len = Array.getLength(collection);
			T[] array = (T[]) Array.newInstance(componentType,len);
			Object v = null;
			Class<?> vclazz = null;
			for(int i=0;i<len;i++){
				v = Array.get(collection, i);
				vclazz = v.getClass();
				if(vclazz.isArray() || TypeUtil.isCollection(vclazz)){
					Array.set(array, i, toArray(v, componentType.getComponentType()));
				}else{
					Array.set(array, i, Array.get(collection, i));
				}
			}
			return array;
		}
		if(TypeUtil.isCollection(clazz)){
			Collection<?> co = (Collection<?>) collection;
			T[] array = (T[]) Array.newInstance(componentType,co.size());
			return co.toArray(array);
		}
		throw new RuntimeException("不支持的类型："+clazz.getName());
	}
}
