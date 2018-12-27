package org.jsirenia.json;
import java.lang.reflect.Type;
import java.util.List;

import com.alibaba.fastjson.TypeReference;

public class ResTypeReference extends TypeReference<Res<List<User>>>{
	public ResTypeReference(Type... actualTypeArguments){
		super(actualTypeArguments);
	}
}
//或者下面方式也可以
/*public class ResTypeReference<E> extends TypeReference<Res<List<E>>>{
	public ResTypeReference(Type... actualTypeArguments){
		super(actualTypeArguments);
	}
}*/

