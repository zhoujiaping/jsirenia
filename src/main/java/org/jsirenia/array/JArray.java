package org.jsirenia.array;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsirenia.util.callback.Callback11;
import org.jsirenia.util.callback.Callback20;
import org.jsirenia.util.callback.Callback21;
import org.jsirenia.util.callback.Callback30;
import org.jsirenia.util.callback.Callback31;
import org.jsirenia.util.callback.Callback41;
/**
 * 非常方便进行map,reduce,filter等操作。js版数组
 * 性能嘛，当然和java的stream没法比。
 * 挺有意思的，操作数据很6很方便
https://www.cnblogs.com/sqh17/p/8529401.html
 */
public class JArray<T> implements Iterable<T>{
	private List<T> list;
	public static <E> JArray<E> of(List<E> list){
		JArray<E> array = new JArray<>();
		array.list = list;
		return array;
	}
	@SafeVarargs
	public static <E> JArray<E> of(E... objects){
		JArray<E> array = new JArray<>();
		array.list = new ArrayList<>();
		if(objects==null){
			return array;
		}
		for(int i=0;i<objects.length;i++){
			array.list.add(objects[i]);
		}
		return array;
	}
	public List<T> toList(){
		return list;
	}
	public <E> JArray<E> map(Callback11<E,T> cb){
		List<E> localList = new ArrayList<>();
		for(int i=0;i<list.size();i++){
			localList.add(cb.apply(list.get(i)));
		}
		return JArray.of(localList);
	}
	public <E> JArray<E> map(Callback21<E,T,Integer> cb){
		List<E> localList = new ArrayList<>();
		for(int i=0;i<list.size();i++){
			localList.add(cb.apply(list.get(i),i));
		}
		return JArray.of(localList);
	}
	public <E> JArray<E> map(Callback31<E,T,Integer,JArray<T>> cb){
		List<E> localList = new ArrayList<>();
		for(int i=0;i<list.size();i++){
			localList.add(cb.apply(list.get(i),i,this));
		}
		return JArray.of(localList);
	}
	public Object[] toArray(){
		return (Object[]) Array.newInstance(Object.class, list.size());
	}
	@SuppressWarnings("unchecked")
	public T[] toArray(Class<T> clazz){
		T[] array = (T[]) Array.newInstance(clazz, list.size());
		for(int i=0;i<array.length;i++){
			array[i] = list.get(i);
		}
		return array;
	}
	public T reduce(Callback31<T,T,T,Integer> cb,T initValue){
		T v = initValue;
		for(int i=0;i<list.size();i++){
			v = cb.apply(v, list.get(i), i);
		}
		return v;
	}
	public T reduce(Callback41<T,T,T,Integer,JArray<T>> cb,T initValue){
		T v = initValue;
		for(int i=0;i<list.size();i++){
			v = cb.apply(v, list.get(i), i ,this);
		}
		return v;
	}
	public T reduce(Callback21<T,T,T> cb,T initValue){
		T v = initValue;
		for(int i=0;i<list.size();i++){
			v = cb.apply(v, list.get(i));
		}
		return v;
	}
	public JArray<T> filter(Callback11<Boolean,T> cb){
		List<T> newList = new ArrayList<>(list.size());
		T v = null;
		for(int i=0;i<list.size();i++){
			v = list.get(i);
			if(cb.apply(v)){
				newList.add(v);
			}
		}
		JArray<T> array = new JArray<>();
		array.list = newList;
		return array;
	}
	public JArray<T> filter(Callback21<Boolean,T,Integer> cb){
		List<T> newList = new ArrayList<>(list.size());
		T v = null;
		for(int i=0;i<list.size();i++){
			v = list.get(i);
			if(cb.apply(v,i)){
				newList.add(v);
			}
		}
		JArray<T> array = new JArray<>();
		array.list = newList;
		return array;
	}
	public JArray<T> filter(Callback31<Boolean,T,Integer,JArray<T>> cb){
		List<T> newList = new ArrayList<>(list.size());
		T v = null;
		for(int i=0;i<list.size();i++){
			v = list.get(i);
			if(cb.apply(v,i,this)){
				newList.add(v);
			}
		}
		JArray<T> array = new JArray<>();
		array.list = newList;
		return array;
	}
	public <R> Map<R,JArray<T>> groupBy(Callback11<R,T> cb){
		//TODO
		Map<R,JArray<T>> map = new HashMap<>();
		Iterator<T> iter = list.listIterator();
		T v;
		R k;
		while(iter.hasNext()){
			v = iter.next();
			k = cb.apply(v);
			
		}
		return null;
	}
	public String join(){
		return join(",");
	}
	public String join(String seperator){
		StringBuilder sb = new StringBuilder();
		Iterator<T> iter = this.iterator();
		T t;
		if(iter.hasNext()){
			t = iter.next();
			sb.append(String.valueOf(t));
		}
		while(iter.hasNext()){
			t = iter.next();
			sb.append(seperator).append(String.valueOf(t));
		}
		return sb.toString();
	}
	public boolean every(Callback31<Boolean,T,Integer,JArray<T>> cb){
		for(int i=0;i<list.size();i++){
			if(!cb.apply(list.get(i), i, this)){
				return false;
			}
		}
		return true;
	}
	public boolean every(Callback21<Boolean,T,Integer> cb){
		for(int i=0;i<list.size();i++){
			if(!cb.apply(list.get(i), i)){
				return false;
			}
		}
		return true;
	}
	public boolean every(Callback11<Boolean,T> cb){
		for(int i=0;i<list.size();i++){
			if(!cb.apply(list.get(i))){
				return false;
			}
		}
		return true;
	}
	public boolean some(Callback31<Boolean,T,Integer,JArray<T>> cb){
		for(int i=0;i<list.size();i++){
			if(cb.apply(list.get(i),i,this)){
				return true;
			}
		}
		return false;
	}
	public boolean some(Callback21<Boolean,T,Integer> cb){
		for(int i=0;i<list.size();i++){
			if(cb.apply(list.get(i),i)){
				return true;
			}
		}
		return false;
	}
	public boolean some(Callback11<Boolean,T> cb){
		for(int i=0;i<list.size();i++){
			if(cb.apply(list.get(i))){
				return true;
			}
		}
		return false;
	}
	public int push(T v1,T v2,T v3,T v4,T v5,T v6,T v7,T v8,T v9,T v10){
		list.add(v1);
		list.add(v2);
		list.add(v3);
		list.add(v4);
		list.add(v5);
		list.add(v6);
		list.add(v7);
		list.add(v8);
		list.add(v9);
		list.add(v10);
		return list.size();
	}
	public int push(T v1,T v2,T v3,T v4,T v5,T v6,T v7,T v8,T v9){
		list.add(v1);
		list.add(v2);
		list.add(v3);
		list.add(v4);
		list.add(v5);
		list.add(v6);
		list.add(v7);
		list.add(v8);
		list.add(v9);
		return list.size();
	}
	public int push(T v1,T v2,T v3,T v4,T v5,T v6,T v7,T v8){
		list.add(v1);
		list.add(v2);
		list.add(v3);
		list.add(v4);
		list.add(v5);
		list.add(v6);
		list.add(v7);
		list.add(v8);
		return list.size();
	}
	public int push(T v1,T v2,T v3,T v4,T v5,T v6,T v7){
		list.add(v1);
		list.add(v2);
		list.add(v3);
		list.add(v4);
		list.add(v5);
		list.add(v6);
		list.add(v7);
		return list.size();
	}
	public int push(T v1,T v2,T v3,T v4,T v5,T v6){
		list.add(v1);
		list.add(v2);
		list.add(v3);
		list.add(v4);
		list.add(v5);
		list.add(v6);
		return list.size();
	}
	public int push(T v1,T v2,T v3,T v4,T v5){
		list.add(v1);
		list.add(v2);
		list.add(v3);
		list.add(v4);
		list.add(v5);
		return list.size();
	}
	public int push(T v1,T v2,T v3,T v4){
		list.add(v1);
		list.add(v2);
		list.add(v3);
		list.add(v4);
		return list.size();
	}
	public int push(T v1,T v2,T v3){
		list.add(v1);
		list.add(v2);
		list.add(v3);
		return list.size();
	}
	public int push(T v1,T v2){
		list.add(v1);
		list.add(v2);
		return list.size();
	}
	public int push(T v1){
		list.add(v1);
		return list.size();
	}
	public T pop(){
		return list.remove(list.size()-1);
	}
	//最多支持10个元素。可变参数版本调用方也会有警告，哎！
	@SuppressWarnings("unchecked")
	public JArray<T> concat(JArray<T> a1,JArray<T> a2,JArray<T> a3,JArray<T> a4,JArray<T> a5,JArray<T> a6,JArray<T> a7,JArray<T> a8,JArray<T> a9,JArray<T> a10){
		return concat(new JArray[]{a1,a2,a3,a4,a5,a6,a7,a8,a9,a10});
	}
	@SuppressWarnings("unchecked")
	public JArray<T> concat(JArray<T> a1,JArray<T> a2,JArray<T> a3,JArray<T> a4,JArray<T> a5,JArray<T> a6,JArray<T> a7,JArray<T> a8,JArray<T> a9){
		return concat(new JArray[]{a1,a2,a3,a4,a5,a6,a7,a8,a9});
	}
	@SuppressWarnings("unchecked")
	public JArray<T> concat(JArray<T> a1,JArray<T> a2,JArray<T> a3,JArray<T> a4,JArray<T> a5,JArray<T> a6,JArray<T> a7,JArray<T> a8){
		return concat(new JArray[]{a1,a2,a3,a4,a5,a6,a7,a8});
	}
	@SuppressWarnings("unchecked")
	public JArray<T> concat(JArray<T> a1,JArray<T> a2,JArray<T> a3,JArray<T> a4,JArray<T> a5,JArray<T> a6,JArray<T> a7){
		return concat(new JArray[]{a1,a2,a3,a4,a5,a6,a7});
	}
	@SuppressWarnings("unchecked")
	public JArray<T> concat(JArray<T> a1,JArray<T> a2,JArray<T> a3,JArray<T> a4,JArray<T> a5,JArray<T> a6){
		return concat(new JArray[]{a1,a2,a3,a4,a5,a6});
	}
	@SuppressWarnings("unchecked")
	public JArray<T> concat(JArray<T> a1,JArray<T> a2,JArray<T> a3,JArray<T> a4,JArray<T> a5){
		return concat(new JArray[]{a1,a2,a3,a4,a5});
	}
	@SuppressWarnings("unchecked")
	public JArray<T> concat(JArray<T> a1,JArray<T> a2,JArray<T> a3,JArray<T> a4){
		return concat(new JArray[]{a1,a2,a3,a4});
	}
	@SuppressWarnings("unchecked")
	public JArray<T> concat(JArray<T> a1,JArray<T> a2,JArray<T> a3){
		return concat(new JArray[]{a1,a2,a3});
	}
	@SuppressWarnings("unchecked")
	public JArray<T> concat(JArray<T> a1,JArray<T> a2){
		return concat(new JArray[]{a1,a2});
	}
	//只有一个的情况，优化一下，这种情况使用概率较高
	public JArray<T> concat(JArray<T> a1){
		List<T> newList = new ArrayList<>(list.size()+a1.list.size());
		newList.addAll(list);
		newList.addAll(a1.list);
		return JArray.of(newList);
	}
	private JArray<T> concat(JArray<T>[] anothers){
		int size = list.size();
		for(int i=0;i<anothers.length;i++){
			size+=anothers[i].list.size();
		}
		List<T> newList = new ArrayList<>(size);
		newList.addAll(list);
		for(int i=0;i<anothers.length;i++){
			newList.addAll(anothers[i].list);
		}
		return JArray.of(newList);
	}
	public JArray<T> concat(T v1,T v2,T v3,T v4,T v5,T v6,T v7,T v8,T v9,T v10){
		List<T> newList = new ArrayList<>(list.size()+10);
		newList.addAll(list);
		newList.add(v1);
		newList.add(v2);
		newList.add(v3);
		newList.add(v4);
		newList.add(v5);
		newList.add(v6);
		newList.add(v7);
		newList.add(v8);
		newList.add(v9);
		newList.add(v10);
		return JArray.of(newList);
	}
	public JArray<T> concat(T v1,T v2,T v3,T v4,T v5,T v6,T v7,T v8,T v9){
		List<T> newList = new ArrayList<>(list.size()+9);
		newList.addAll(list);
		newList.add(v1);
		newList.add(v2);
		newList.add(v3);
		newList.add(v4);
		newList.add(v5);
		newList.add(v6);
		newList.add(v7);
		newList.add(v8);
		newList.add(v9);
		return JArray.of(newList);
	}
	public JArray<T> concat(T v1,T v2,T v3,T v4,T v5,T v6,T v7,T v8){
		List<T> newList = new ArrayList<>(list.size()+8);
		newList.addAll(list);
		newList.add(v1);
		newList.add(v2);
		newList.add(v3);
		newList.add(v4);
		newList.add(v5);
		newList.add(v6);
		newList.add(v7);
		newList.add(v8);
		return JArray.of(newList);
	}
	public JArray<T> concat(T v1,T v2,T v3,T v4,T v5,T v6,T v7){
		List<T> newList = new ArrayList<>(list.size()+7);
		newList.addAll(list);
		newList.add(v1);
		newList.add(v2);
		newList.add(v3);
		newList.add(v4);
		newList.add(v5);
		newList.add(v6);
		newList.add(v7);
		return JArray.of(newList);
	}
	public JArray<T> concat(T v1,T v2,T v3,T v4,T v5,T v6){
		List<T> newList = new ArrayList<>(list.size()+6);
		newList.addAll(list);
		newList.add(v1);
		newList.add(v2);
		newList.add(v3);
		newList.add(v4);
		newList.add(v5);
		newList.add(v6);
		return JArray.of(newList);
	}
	public JArray<T> concat(T v1,T v2,T v3,T v4,T v5){
		List<T> newList = new ArrayList<>(list.size()+5);
		newList.addAll(list);
		newList.add(v1);
		newList.add(v2);
		newList.add(v3);
		newList.add(v4);
		newList.add(v5);
		return JArray.of(newList);
	}
	public JArray<T> concat(T v1,T v2,T v3,T v4){
		List<T> newList = new ArrayList<>(list.size()+4);
		newList.addAll(list);
		newList.add(v1);
		newList.add(v2);
		newList.add(v3);
		newList.add(v4);
		return JArray.of(newList);
	}
	public JArray<T> concat(T v1,T v2,T v3){
		List<T> newList = new ArrayList<>(list.size()+3);
		newList.addAll(list);
		newList.add(v1);
		newList.add(v2);
		newList.add(v3);
		return JArray.of(newList);
	}
	public JArray<T> concat(T v1,T v2){
		List<T> newList = new ArrayList<>(list.size()+2);
		newList.addAll(list);
		newList.add(v1);
		newList.add(v2);
		return JArray.of(newList);
	}
	public JArray<T> concat(T v1){
		List<T> newList = new ArrayList<>(list.size()+1);
		newList.addAll(list);
		newList.add(v1);
		return JArray.of(newList);
	}
	/*private JArray<T> concat(T[] values){
		if(values==null){
			throw new RuntimeException("anothers can not be null");
		}
		List<T> newList = new ArrayList<>(list.size()+values.length);
		newList.addAll(list);
		for(int i=0;i<values.length;i++){
			newList.add(values[i]);
		}
		return JArray.of(newList);
	}*/
	public int unshift(){
		throw new RuntimeException("method unshift is not supported!");
	}
	public T shift(){
		throw new RuntimeException("method shift is not supported!");
	}
	public JArray<T> splice(){
		throw new RuntimeException("method splice is not supported!");
	}
	public JArray<T> slice(int start){
		return slice(start,list.size());
	}
	public JArray<T> slice(int start, int end){
		int size = list.size();
		if(start<0){
			start = start+size;
		}
		if(end<0){
			end = end+size; 
		}
		int newSize = end-start;
		List<T> newList = null;
		if(newSize>0){
			newList = new ArrayList<>(newSize);
		}else{
			newList = new ArrayList<>();
		}
		for(int i=start;i<end;i++){
			newList.add(list.get(i));
		}
		return JArray.of(newList);
	}
	public void sort(Comparator<? super T> c){
		list.sort(c);
	}
	@SuppressWarnings("unchecked")
	public void sort(){
		list.sort((v1,v2)->{
			Comparable<T> c1 = (Comparable<T>) v1;
			return c1.compareTo(v2);
		});
	}
	public JArray<T> reverse(){
		Collections.reverse(list);
		return this;
	}
	public T reduceRight(Callback41<T,T,T,Integer,JArray<T>> cb,T initValue){
		T v = initValue;
		for(int i=list.size()-1;i>=0;i--){
			v = cb.apply(v, list.get(i), i ,this);
		}
		return v;
	}
	public T reduceRight(Callback31<T,T,T,Integer> cb,T initValue){
		T v = initValue;
		for(int i=list.size()-1;i>=0;i--){
			v = cb.apply(v, list.get(i), i);
		}
		return v;
	}
	public T reduceRight(Callback21<T,T,T> cb,T initValue){
		T v = initValue;
		for(int i=list.size()-1;i>=0;i--){
			v = cb.apply(v, list.get(i));
		}
		return v;
	}
	public int lastIndexOf(T t){
		return list.lastIndexOf(t);
	}
	public int indexOf(T t){
		return list.indexOf(t);
	}
	public T find(Callback11<Boolean,T> cb){
		for(int i=0;i<list.size();i++){
			if(cb.apply(list.get(i))){
				return list.get(i);
			}
		}
		return null;
	}
	public int findIndex(Callback11<Boolean,T> cb){
		for(int i=0;i<list.size();i++){
			if(cb.apply(list.get(i))){
				return i;
			}
		}
		return -1;
	}
	public JArray<T> fill(T t,int start,int end){
		int size = list.size();
		if(start<0){
			start = start+size;
		}
		if(end<0){
			end = end+size; 
		}
		for(int i=start;i<end;i++){
			list.set(i, t);
		}
		return this;
	}
	public void forEach(Callback20<T,Integer> cb){
		for(int i=0;i<list.size();i++){
			cb.apply(list.get(i),i);
		}
	}
	public void forEach(Callback30<T,Integer,JArray<T>> cb){
		for(int i=0;i<list.size();i++){
			cb.apply(list.get(i),i,this);
		}
	}
	public boolean inclueds(){
		throw new RuntimeException("method inclueds is not supported!");
	}
	public JArray<Integer> keys(){
		throw new RuntimeException("method keys is not supported!");
	}

	@Override
	public Iterator<T> iterator() {
		return list.listIterator();
	}
	public static void main(String[] args) {
		String[] array = JArray.of("hello","world").toArray(String.class);
		System.out.println(array);
		JArray<String> jarray = JArray.of("hello","world","jsirenia");
		String res = jarray.filter(i->i.contains("l")).map(t->{
			return t.toUpperCase();
		}).reduce((prev,curr)->prev+" "+curr, "");
		System.out.println(res);
		for(String item : jarray){
			System.out.println(item);
		}
		JArray<String> jarray2 = JArray.of(new String[]{"hello","world","jsirenia"});
		System.out.println(jarray2.join(","));
		String s = jarray.concat(jarray2).join(",");
		System.out.println(s);
		
		jarray.sort();
		System.out.println(jarray.join());
	}
}
