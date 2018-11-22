package org.jsirenia.bean;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchData<T> implements Iterator<T>{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private List<T> data;
	private int index=0;
	private int fetchSize = 1000;
	private T prevItem = null;
	private int total = 0;
	private int fetchTimes = 0;
	private final Instant begin = Instant.now();
	private String label = "";
	private Producer<T> producer;
	public BatchData(String label){
		this.label = label;
	}
	public BatchData<T> withFetchSize(int fetchSize){
		this.fetchSize = fetchSize;
		return this;
	}
	protected void fillData(T prevItem,int fetchSize){
		fetchTimes++;
		data = producer.produce(prevItem, fetchSize);
		if(!(data instanceof ArrayList)){
			throw new RuntimeException("result of produceData only support ArrayList");
		}
		index = 0;
		total += data.size();
		Instant end = Instant.now();
		Duration duration = Duration.between(begin, end);
		logger.info("{}：第{}次读取数据，读取到{}条，已读{}条，共耗时{}分钟/{}毫秒",label,fetchTimes,data.size(),total,duration.toMinutes(), duration.toMillis());
	}
	public static <E> BatchData<E> withProducer(String label,Producer<E> producer){
		BatchData<E> bd = new BatchData<>(label);
		bd.producer = producer;
		return bd;
	}
	public static interface Producer<T>{
		List<T> produce(T prevItem,int fetchSize);
	}
	@Override
	public boolean hasNext() {
		if(data==null){
			fillData(prevItem,fetchSize);
			return !data.isEmpty();
		}else{
			boolean hasMore =  index < data.size() || fetchSize==data.size();
			if(hasMore){
				if(index>=data.size()){
					fillData(prevItem, fetchSize);
					if(data.isEmpty()){
						return false;
					}
				}
			}
			return hasMore;
		}
	}

	@Override
	public T next() {
		T item = data.get(index);
		index++;
		prevItem = item;
		return item;
	}
	public static void main(String[] args) {
		BatchData.withProducer("test", (Integer prev,int fetchSize)->{
			if(prev==null){
				prev=0;
			}
			List<Integer> list = new ArrayList<>();
			for(int i=1;i<=fetchSize && prev+i<95;i++){
				list.add(prev+i);
			}
			return list;
		}).withFetchSize(10).forEach(item->{
			System.out.println(item);
		});
	}
	public void forEach(Consumer<? super T> consume){
		while(hasNext()){
			consume.accept(next());
		}
	}
}
