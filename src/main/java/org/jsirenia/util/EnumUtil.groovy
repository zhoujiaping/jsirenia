package org.jsirenia.util

import org.springframework.http.HttpMethod

class EnumUtil {
    /**
     * 返回枚举名称到枚举实例的映射
     * */
    static Map toMap(Class enumClass){
        def map = new LinkedHashMap()
        enumClass.enumConstants.each{
            map[it.name()] = it
        }
        map
    }

    public static void main(String[] args) {
        println toMap(HttpMethod)
    }
}
