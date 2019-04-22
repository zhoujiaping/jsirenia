package org.jsirenia.queue;

import java.util.List;

public interface RedisQueue {

	void clear();

	boolean isEmpty();

	String peek();

	int add(String... source);

	List<String> peek(int count);

	List<String> take(int count);

	String take();

	int size();

}