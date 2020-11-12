package org.jsirenia.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志脱敏
 */
public abstract class JsonLogUtil {
    private static final int maxMaskLen = 6;
    static String mobileNoRegex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";
    static Pattern mobileNoPattern = Pattern.compile(mobileNoRegex);


    /**
     * 简单优化一下打印，身份证和手机号及银行卡敏感信息打印
     */
    public static Function<Object, String> maskFunc = o -> {
        if (null == o) {
            return null;
        }
        String source = String.valueOf(o);
        String target = "******";
        if (StringUtils.isBlank(source)) {
            return "";
        }
        //手机号
        int len = source.length();
        if (len == 11) {
            Matcher m = mobileNoPattern.matcher(source);
            boolean isMatch = m.matches();
            if (isMatch) {
                target = source.substring(0,3)+"****"+source.substring(len-3);
                //target = source.replaceAll("(\\w{3})\\w*(\\w{4})", "$1****$2");
            }
        } else if (source.length() == 18) {
            //身份证号（这里简单校验是否为18位，详细校验是没有必要性）
            target = StringUtils.left(source, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(source, 3), StringUtils.length(source), "*"), "******"));
        } else if (source.length() > 11) {
            //其他大于11位，显示4位尾数
            target = "******" + StringUtils.substring(source, -4);
        }
        return target;
    };

    /**
     * 序列化时过滤字段输出,所有<code>ignoreFields<code/>属性都不会序列化,包括obj引用的对象属性
     * <p>仅供日志打印方便,不建议在业务中使用
     *
     * @param obj          要转换为json字符串的对象
     * @param ignoreFields 要忽略的属性字段,会以******显示
     * @return json序列化后的字符串
     */
    public static String logObject(Object obj, String... ignoreFields) {
        return logObject(obj, Sets.newHashSet(ignoreFields), maskFunc);
    }
    public static String logObject(Object obj, Set<String> ignoreFields) {
        return logObject(obj, ignoreFields, maskFunc);
    }
    static String logObject(Object obj, Set<String> maskFields, Function<Object, String> maskFunc) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        boolean mask = (maskFields != null) && (maskFields.size() > 0) && (maskFunc != null);
        ValueFilter valueFilter = (o, propertyName, propertyValue) -> {
            if (mask && propertyValue != null) {
                if (propertyValue instanceof String) {
                    // 如果是json字符串,递归处理
                    boolean isJson = isJsonString((String) propertyValue);
                    if (isJson) {
                        return logObject(propertyValue, maskFields, maskFunc);
                    }
                }
                if(maskFields.contains(propertyName)){
                    return maskFunc.apply(propertyValue);
                }
            }
            return propertyValue;
        };
        return JSON.toJSONString(obj, valueFilter, SerializerFeature.DisableCircularReferenceDetect);
    }
    /**
     * 是否为json字符串
     *
     * @param str 字符串
     * @return 是-true,否-false
     */
    private static boolean isJsonString(String str) {
        if (str == null || str.length() < 2) {
            return false;
        }
        char start = str.charAt(0);
        char end = str.charAt(str.length() - 1);
        // 先简单判断是不是json字符串
        boolean isJsonObj = (start == '{' && end == '}');
        boolean isJsonArray = (start == '[' && end == ']');
        if (isJsonObj || isJsonArray) {
            try {
                JSON.parse(str);
                return true;
            } catch (Exception ignore) {
                // 不是json字符串
            }
        }
        return false;
    }
}