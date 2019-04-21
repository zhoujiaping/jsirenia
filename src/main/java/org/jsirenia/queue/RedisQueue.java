package org.jsirenia.queue;

import java.util.Collection;

public interface RedisQueue {

	boolean addAll(Collection<? extends String> arg0);

	void clear();

	boolean isEmpty();

	Object[] toArray();

	boolean add(String e);

	boolean offer(String e);

	String peek();

}