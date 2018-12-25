package org.jsirenia.reflect;

import java.util.Collection;

public class TypeUtil {
    public static boolean isArray(Object obj){
        Class<?> clazz = obj.getClass();
        return clazz.isArray();
    }
    public static boolean isCollection(Object obj){
        Class<?> clazz = obj.getClass();
        return Collection.class.isAssignableFrom(clazz);
    }
}
