package org.jsirenia.array;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.jsirenia.util.Callback.Callback11;
import org.jsirenia.util.Callback.Callback20;
import org.jsirenia.util.Callback.Callback21;
import org.jsirenia.util.Callback.Callback30;
import org.jsirenia.util.Callback.Callback31;
import org.jsirenia.util.Callback.Callback41;

/**
 *
 * 这个接口定义，是按照javascript的Array定义的
 * @author zhoujiaping
 */
public interface Array<T> extends Iterable<T>{
	/**
	 * 转换成jdk内置的list类型
	 * @return
	 */
		public List<T> toList();
		public <E> Array<E> map(Callback11<E,T> cb);
		public <E> Array<E> map(Callback21<E,T,Integer> cb);
		public <E> Array<E> map(Callback31<E,T,Integer,Array<T>> cb);
		public Object[] toArray();
		public T[] toArray(Class<T> clazz);
		public T reduce(Callback31<T,T,T,Integer> cb,T initValue);
		public T reduce(Callback41<T,T,T,Integer,Array<T>> cb,T initValue);
		public T reduce(Callback21<T,T,T> cb,T initValue);
		public Array<T> filter(Callback11<Boolean,T> cb);
		public Array<T> filter(Callback21<Boolean,T,Integer> cb);
		public Array<T> filter(Callback31<Boolean,T,Integer,Array<T>> cb);
		public <R> Map<R,Array<T>> groupBy(Callback11<R,T> cb);
		public <R> Map<R,Array<T>> groupBy(Callback21<R,T,Integer> cb);
		public <R> Map<R,Array<T>> groupBy(Callback31<R,T,Integer,Array<T>> cb);
		public String join();
		public String join(String seperator);
		public boolean every(Callback31<Boolean,T,Integer,Array<T>> cb);
		public boolean every(Callback21<Boolean,T,Integer> cb);
		public boolean every(Callback11<Boolean,T> cb);
		public boolean some(Callback31<Boolean,T,Integer,Array<T>> cb);
		public boolean some(Callback21<Boolean,T,Integer> cb);
		public boolean some(Callback11<Boolean,T> cb);
		public int push(T v1,T v2,T v3,T v4,T v5,T v6,T v7,T v8,T v9,T v10);
		public int push(T v1,T v2,T v3,T v4,T v5,T v6,T v7,T v8,T v9);
		public int push(T v1,T v2,T v3,T v4,T v5,T v6,T v7,T v8);
		public int push(T v1,T v2,T v3,T v4,T v5,T v6,T v7);
		public int push(T v1,T v2,T v3,T v4,T v5,T v6);
		public int push(T v1,T v2,T v3,T v4,T v5);
		public int push(T v1,T v2,T v3,T v4);
		public int push(T v1,T v2,T v3);
		public int push(T v1,T v2);
		public int push(T v1);
		public T pop();
		public Array<T> concat(Array<T> a1,Array<T> a2,Array<T> a3,Array<T> a4,Array<T> a5,Array<T> a6,Array<T> a7,Array<T> a8,Array<T> a9,Array<T> a10);
		public Array<T> concat(Array<T> a1,Array<T> a2,Array<T> a3,Array<T> a4,Array<T> a5,Array<T> a6,Array<T> a7,Array<T> a8,Array<T> a9);
		public Array<T> concat(Array<T> a1,Array<T> a2,Array<T> a3,Array<T> a4,Array<T> a5,Array<T> a6,Array<T> a7,Array<T> a8);
		public Array<T> concat(Array<T> a1,Array<T> a2,Array<T> a3,Array<T> a4,Array<T> a5,Array<T> a6,Array<T> a7);
		public Array<T> concat(Array<T> a1,Array<T> a2,Array<T> a3,Array<T> a4,Array<T> a5,Array<T> a6);
		public Array<T> concat(Array<T> a1,Array<T> a2,Array<T> a3,Array<T> a4,Array<T> a5);
		public Array<T> concat(Array<T> a1,Array<T> a2,Array<T> a3,Array<T> a4);
		public Array<T> concat(Array<T> a1,Array<T> a2,Array<T> a3);
		public Array<T> concat(Array<T> a1,Array<T> a2);
		public Array<T> concat(Array<T> a1);
		public Array<T> concat(T v1,T v2,T v3,T v4,T v5,T v6,T v7,T v8,T v9,T v10);
		public Array<T> concat(T v1,T v2,T v3,T v4,T v5,T v6,T v7,T v8,T v9);
		public Array<T> concat(T v1,T v2,T v3,T v4,T v5,T v6,T v7,T v8);
		public Array<T> concat(T v1,T v2,T v3,T v4,T v5,T v6,T v7);
		public Array<T> concat(T v1,T v2,T v3,T v4,T v5,T v6);
		public Array<T> concat(T v1,T v2,T v3,T v4,T v5);
		public Array<T> concat(T v1,T v2,T v3,T v4);
		public Array<T> concat(T v1,T v2,T v3);
		public Array<T> concat(T v1,T v2);
		public Array<T> concat(T v1);
		public int unshift();
		public T shift();
		public Array<T> splice();
		public Array<T> slice(int start);
		public Array<T> slice(int start, int end);
		public void sort(Comparator<? super T> c);
		public void sort();
		public Array<T> reverse();
		public T reduceRight(Callback41<T,T,T,Integer,Array<T>> cb,T initValue);
		public T reduceRight(Callback31<T,T,T,Integer> cb,T initValue);
		public T reduceRight(Callback21<T,T,T> cb,T initValue);
		public int lastIndexOf(T t);
		public int indexOf(T t);
		public T find(Callback11<Boolean,T> cb);
		public int findIndex(Callback11<Boolean,T> cb);
		public Array<T> fill(T t,int start,int end);
		public void forEach(Callback20<T,Integer> cb);
		public void forEach(Callback30<T,Integer,Array<T>> cb);
		public boolean inclueds();
		public Array<Integer> keys();
	}
