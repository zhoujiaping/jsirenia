package org.jsirenia.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class JsonLogUtil {
    private static final int maxMaskLen = 6;

    private JsonLogUtil() {}

    /**
     * 简单优化一下打印，身份证和手机号及银行卡敏感信息打印
     */
    public static Function<Object, String> maskFunc = new Function<Object, String>() {
        @Override
        public String apply(Object o) {
            if(null == o){
                return null;
            }
            String source = String.valueOf(o);
            String target = "******";
            if(StringUtils.isBlank(source)){
                return "";
            }
            //手机号
            String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";
            if(source.length() == 11){
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(source);
                boolean isMatch = m.matches();
                if(isMatch){
                    target = source.replaceAll("(\\w{3})\\w*(\\w{4})", "$1****$2");
                }
            }else if(source.length() == 18){
                //身份证号（这里简单校验是否为18位，详细校验是没有必要性）
                target = StringUtils.left(source, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(source, 3), StringUtils.length(source), "*"), "******"));
            }else if(source.length() > 11){
                //其他大于11位，显示4位尾数
                target = "******" + StringUtils.substring(source, -4);
            }

            return target ;
        }
    };

    /**
     * 序列化时过滤字段输出,所有<code>ignoreFields<code/>属性都不会序列化,包括obj引用的对象属性
     * <p>仅供日志打印方便,不建议在业务中使用
     * @param obj 要转换为json字符串的对象
     * @param ignoreFields 要忽略的属性字段,会以******显示
     * @return json序列化后的字符串
     */
    public static String logObject(Object obj, String... ignoreFields){
        return logObject(obj, ignoreFields, maskFunc);
    }

    /**
     * 序列化时过滤字段输出,所有<code>ignoreFields<code/>属性都不会序列化,包括obj引用的对象属性
     * <p>仅供日志打印方便,不建议在业务中使用
     * @param obj 要转换为json字符串的对象
     * @param maskFields 要脱敏的属性字段
     * @param maskFunc 处理脱敏字段的函数
     * @return json序列化后的字符串
     */
    public static String logObject(Object obj, String[] maskFields, Function<Object, String> maskFunc){
        if(obj == null){
            return null;
        }
        if(obj instanceof String){
            return (String)obj;
        }
        boolean mask = (maskFields != null) && (maskFields.length > 0) && (maskFunc != null);
        ValueFilter valueFilter = (o, propertyName, propertyValue) -> {
            if(mask && propertyValue != null){
                if(propertyValue instanceof String){
                    // 如果是json字符串,递归处理
                    boolean isJson = isJsonString((String) propertyValue);
                    if(isJson){
                        return logObject(propertyValue, maskFields);
                    }
                }
                for (String maskField : maskFields) {
                    if (Objects.equals(maskField, propertyName)) {
                        return maskFunc.apply(propertyValue);
                    }
                }
            }
            return propertyValue;
        };
        return JSON.toJSONString(obj, valueFilter, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 序列化时对象为json字符串
     * <p>所有指定的<code>maskFields<code/>属性值会进行简单脱敏(长度大于7的显示前3后4位,长度小于7的显示第一位),包括obj引用的对象属性
     * <p>
     * @param obj 要序列化为json字符串的对象
     * @param maskFields 要脱敏的属性字段
     * @return json序列化后的字符串
     */
    public static String logGeneralObject(Object obj, String... maskFields){
        return logObject(obj, maskFields, v -> {
            if (v instanceof String) {
                final String val = (String) v;
                if (val.length() > 7) {
                    return dataMasking(v.toString(), 3, 4);
                }
                return dataMasking(v.toString(), 0, 1);
            }
            return String.valueOf(v);
        });
    }

    /**
     * 是否为json字符串
     * @param str 字符串
     * @return 是-true,否-false
     */
    private static boolean isJsonString(String str){
        if(str == null || str.length() < 2){
            return false;
        }
        char start = str.charAt(0);
        char end = str.charAt(str.length() - 1);
        // 先简单判断是不是json字符串
        boolean isJsonObj = (start == '{' && end == '}');
        boolean isJsonArray = (start == '[' && end == ']');
        if(isJsonObj || isJsonArray){
            try {
                JSON.parse(str);
                return true;
            } catch (Exception ignore){
                // 不是json字符串
            }
        }
        return false;
    }


    /**
     * @param s 需要脱敏的字符串
     * @param prefix 保留前几位
     * @param suffix 保留的最后几位
     * @return mask后的数据
     */
    private static String dataMasking(final String s, int prefix, int suffix){
        if(s == null || s.length() == 0){
            return s;
        }
        int len = s.length();
        if(len <= (prefix + suffix)){
            return s;
        }
        StringBuilder buffer = new StringBuilder(prefix + maxMaskLen + suffix);
        if(prefix > 0){
            buffer.append(s, 0, prefix);
        }
        for(int i = 0; i < len - (prefix + suffix); ++i){
            if(i >= maxMaskLen){
                break;
            }
            buffer.append("*");
        }
        if(suffix > 0){
            buffer.append(s, len - suffix, len);
        }
        return buffer.toString();
    }
}