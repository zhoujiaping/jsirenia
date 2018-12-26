import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JSONTest {

    @Test
    public void test1() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        String text = "[1,1,1]";
        Class<?> clazz = new Object[]{}.getClass();
        ParserConfig parseconfig = new ParserConfig();
        parseconfig.setAutoTypeSupport(true);
        text = "[{\"@type\":\"User\",\"name\":\"z\"},{\"@type\":\"User\",\"name\":\"x\"}]";
        clazz = List.class;
        //fastjson从带类型信息的json反序列化为java类
        //如果parseconfig没有设置支持类型，同时文本中有@type，那么会抛出异常
        Object res = JSON.parseObject(text,clazz,parseconfig);
        System.out.println(res);
        //fastjson序列化带类型信息的json
        text = JSON.toJSONString(res,SerializerFeature.WriteClassName);
        System.out.println(text);
        Method m = User.class.getMethod("sayHello",String.class,int.class,char.class);
        Object r = m.invoke(new User(), null,1,'c');
        System.out.println(r);
    }
}
