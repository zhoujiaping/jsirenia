package org.jsirenia.queue;

public interface RedisQueue {

	void clear();

	boolean isEmpty();

	Object[] toArray();

	boolean offer(String e);

	String peek();

	boolean add(String... source);

}