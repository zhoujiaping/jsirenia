package org.jsirenia.array;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsirenia.util.callback.Callback10;
import org.jsirenia.util.callback.Callback11;
import org.jsirenia.util.callback.Callback21;
/**
 * 非常方便进行map,reduce,filter等操作。js版数组，但是
 * 函数副作用方面有所不同
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
	public static <E> JArray<E> of(E... objects){
		if(objects==null){
			throw new RuntimeException("objects can not be null");
		}
		JArray<E> array = new JArray<>();
		array.list = new ArrayList<>();
		for(int i=0;i<objects.length;i++){
			array.list.add(objects[i]);
		}
		return array;
	}
	public List<T> toList(){
		return list;
	}
	public void forEach(Callback10<T> callback){
		Iterator<T> iter = list.iterator();
		while(iter.hasNext()){
			callback.apply(iter.next());
		}
	}
	public <E> JArray<E> map(Callback11<E,T> cb){
		List<E> localList = new ArrayList<>();
		for(int i=0;i<list.size();i++){
			localList.add(cb.apply(list.get(i)));
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
	public Object groupBy(){
		//TODO
		return null;
	}
	public String join(){
		//TODO
		return null;
	}
	public boolean every(){
		//TODO
		return false;
	}
	public boolean any(){
		//TODO
		return false;
	}
	public boolean some(){
		//TODO
		return false;
	}
	public int indexOf(){
		//TODO
		return -1;
	}
	public int push(T t){
		//TODO
		return -1;
	}
	public T pop(){
		//TODO
		return null;
	}
	public JArray<T> concat(){
		//TODO
		return null;
	}
	public int unshift(){
		//TODO
		return -1;
	}
	public T shift(){
		//TODO
		return null;
	}
	public JArray<T> splice(){
		//TODO
		return this;
	}
	public JArray<T> slice(){
		//TODO
		return this;
	}
	public void sort(){
		//TODO
	}
	public JArray<T> reverse(){
		//TODO
		return null;
	}
	public Object reduceRight(){
		//TODO
		return null;
	}
	public int lastIndexOf(){
		//TODO
		return -1;
	}
	public int findIndex(){
		//TODO
		return -1;
	}
	public void fill(){
		//TODO
	}
	public boolean inclueds(){
		//TODO
		return false;
	}
	public JArray<Integer> keys(){
		//TODO
		return null;
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
	}
	@Override
	public Iterator<T> iterator() {
		return list.listIterator();
	}
}
