package org.jsirenia.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsirenia.reflect.TypeUtil;

public class CollectionUtil {
	/**
	//数组转数组。支持多维数组，比如Object[][]类型转Map<String,String>[][],前提是元素的值真的是Map类型的。
	 * 例如：
	 * 支持Object[]转String[]，前提是Object[]里面的元素的确都是String类型的。componentType=String.class
	 * 支持Object[][]转String[][]。componentType=String[].class
	 * 支持Object[][][][]...[]转String[][][][]...[]。componentType=String[][][]...[].class
	 * 支持Collection<String>转String[]。componentType=String.class
	 * 支持Collection<String[]>转String[][]。componentType=String[].class
	 * 支持Collection<Object>转String[]。前提是集合元素都是String类型的。
	 * 不支持Collection<Object[]>转String[][]，但是可以转Object[][]
	 * 不支持Collection[]转String[][]、Object[][]。但是可以转List[]，前提是Collection的实现类型真的是List
	 * @param collection 源数组、集合
	 * @param componentType 目标数组的元素类型
	 * @return
	 */
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
				if(vclazz.isArray()){
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
	public static void main(String[] args) {
		//Object[][]转Map[][]
		Object[][] collection = new Object[1][1];
		collection[0] = new Object[1];
		String s = "123456";
		collection[0][0] = s;
		String[][] mapArray = toArray(collection, String[].class);
		System.out.println(mapArray);
		//Collection<Object[]>转String[][]
		List<Object[]> list = new ArrayList<>();
		list.add(new String[]{"123456"});
		Object res = toArray(list,String[].class);
		System.out.println(res);
		//Collection<Object>[]转List[]
		List[] listArray = new List[1];
		listArray[0]=new ArrayList<>();
		listArray[0].add("123456");
		res = toArray(listArray,List.class);
		System.out.println(res);
		//Collection<Object>转String[]
		Collection<Object> secondList = new ArrayList<>();
		secondList.add("abcdefg");
		res = toArray(secondList, String.class);
		System.out.println(res);
	}
}
