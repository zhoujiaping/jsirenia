package org.jsirenia.json;


import java.lang.reflect.Type;
import java.util.Map;

import com.alibaba.fastjson.TypeReference;

public class MapTypeReference<K,V> extends TypeReference<Map<K,V>>{
	public MapTypeReference(Type... actualTypeArguments){
		super(actualTypeArguments);
	}
}
