package org.jsirenia.json;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;

public class JSONTypeUtil {
	private static TypeReference<?> typeReference = new TypeReference<Object>(){};
	private static ConcurrentMap<Type, Type> classTypeCache;
	static{
		try {
			Field field = TypeReference.class.getDeclaredField("classTypeCache");
			field.setAccessible(true);
			classTypeCache = (ConcurrentMap<Type, Type>) field.get(null);
			field.setAccessible(false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static Type createType(ParameterizedType parameterizedType){
        Type rawType = parameterizedType.getRawType();
        /* ProxyFactory fac = new ProxyFactory();
        Object proxy = fac.createProxy((Class<?>)rawType, new MethodInterceptor() {
			@Override
			public Object intercept(Object paramObject, Method paramMethod, Object[] paramArrayOfObject,
					MethodProxy paramMethodProxy) throws Throwable {
				return paramMethodProxy.invokeSuper(paramObject, paramArrayOfObject);
			}
		});*/
        Type[] argTypes = parameterizedType.getActualTypeArguments();

        Type key = new ParameterizedTypeImpl(argTypes, typeReference.getClass() , rawType);
        Type cachedType = classTypeCache.get(key);
        if (cachedType == null) {
            classTypeCache.putIfAbsent(key, key);
            cachedType = classTypeCache.get(key);
        }
        return cachedType;

    }
}
