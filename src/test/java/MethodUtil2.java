

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodUtil2 {
	/**
	 * 执行对象的方法，方法参数使用字符串，方法会自动将字符串转换成java对象。
	 * 不支持方法重载
	 * 方法参数不能太复杂
	 * @param target
	 * @param methodName 方法名
	 * @param args
	 * @return
	 * @throws Exception 
	 */
	public static Object invoke(Object target,String className,String methodName,String args) throws Exception{
		Method m = findMethod(className,methodName);
		return invoke(target,m, args);
	}
	public static Method findMethod(String className,String methodName) throws ClassNotFoundException{
		Class<?> type = Class.forName(className);
		if(type==null){
			throw new RuntimeException("类型未找到："+className);
		}
		Method[] methods = type.getDeclaredMethods();
		List<Method> methodList = new ArrayList<Method>(2);
		if(methods!=null){
			for(Method m : methods){
				if(m.getName().equals(methodName)){
					methodList.add(m);
				}
			}
		}
		if(methodList.isEmpty()){
			throw new RuntimeException("方法未找到："+methodName);
		}
		if(methodList.size()>1){
			throw new RuntimeException("存在多个方法："+methodName);
		}
		return methodList.get(0);
	}
	/**
	 * 执行对象的方法，方法参数使用字符串，方法会自动将字符串转换成java对象。
	 * 不支持方法重载
	 * 方法参数不能太复杂
	 * @param target
	 * @param method
	 * @param args
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static Object invoke(Object target,Method method,String args) throws Exception{
		Object[] methodArgs = null;//parseArgs(method,args);
		return method.invoke(target, methodArgs);
	}
	/*public static Object[] parseArgs(String className,String methodName,String args) throws ClassNotFoundException{
		Method m = findMethod(className,methodName);
		return parseArgs(m,args);
	}
	public static Object[] parseArgs(Method method,String args){
		Class<?>[] parameterTypes = method.getParameterTypes();
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		List<Object> argList = JSONArray.parseArray(args);
		Object[] methodArgs = new Object[parameterTypes.length];
		for(int i=0;i<parameterTypes.length;i++){
			methodArgs[i] = JSONUtil.toJavaObject(argList.get(i), parameterTypes[i],genericParameterTypes[i]);
		}
		return methodArgs;
	}*/
}
