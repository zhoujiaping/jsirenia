package org.jsirenia.dubbo;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.jsirenia.defer.Defer;
import org.jsirenia.reflect.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.ValueFilter;

public class FastJsonLogUtil {
	private static Logger logger = LoggerFactory.getLogger(FastJsonLogUtil.class);
	// 入参 json序列化配置 脱敏，web组件处理
	// 出参 json序列化配置 脱敏，分页处理，集合处理
	private static SerializeConfig inConfig = new SerializeConfig();
	private static SerializeConfig outConfig = new SerializeConfig();
	private static final Map<String, Desensitizer> desensitizers = new ConcurrentHashMap<>();
	private static SerializeFilter desensitizerFilter;
	private static SerializeFilter collectionFilter;
	private static ObjectSerializer toStringSerializer;
	static {
		init();
	}

	private static void init() {
		desensitizerFilter = new ValueFilter() {
			@Override
			public Object process(Object object, String name, Object value) {
				Desensitizer desensitizer = desensitizers.get(name);
				if (desensitizer == null) {
					return value;
				}
				if(value instanceof String){
					return desensitizer.apply((String)value);
				}
				return value;
			}
		};
		toStringSerializer = new ObjectSerializer() {
			@Override
			public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
					throws IOException {
				serializer.write(object.toString());
			}
		};
		collectionFilter = new ValueFilter() {
			@Override
			public Object process(Object object, String name, Object value) {
				// 只对ArrayList和HashSet两种类型处理，对别的集合类型，将其都打印出来。
				if (value instanceof ArrayList) {
					Collection<?> c = (Collection<?>) value;
					return "ArrayList.size=" + c.size();
				} else if (value instanceof HashSet) {
					Collection<?> c = (Collection<?>) value;
					return "HashSet.size=" + c.size();
				} else {
					return value;
				}
			}
		};
	}

	public static SerializeConfig getOutSerializeConfig() {
		Defer.once(FastJsonLogUtil.class.getName()+"#getOutSerializeConfig", () -> {
			try {
				setOutConfig();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return outConfig;
	}

	public static SerializeConfig getInSerializeConfig() {
		Defer.once(FastJsonLogUtil.class.getName()+"#getInSerializeConfig", () -> {
			try {
				setInConfig();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return inConfig;
	}

	private static void setInConfig() throws ClassNotFoundException, IOException, URISyntaxException {
		// 处理web组件的序列化
		List<String> classNames = new ArrayList<>();
		classNames.add("javax.servlet.ServletRequestWrapper");
		classNames.add("javax.servlet.ServletResponseWrapper");
		classNames.add("org.apache.catalina.connector.Request");
		classNames.add("org.apache.catalina.connector.Response");
		classNames.add("org.apache.catalina.connector.RequestFacade");
		classNames.add("org.apache.catalina.connector.ResponseFacade");
		classNames.add("org.apache.catalina.session.StandardSession");
		classNames.add("org.apache.catalina.session.StandardSessionFacade");

		for (String name : classNames) {
			if (StringUtils.hasText(name)) {
				try {
					Class<?> clazz = Class.forName(name.trim());
					inConfig.put(clazz, toStringSerializer);
				} catch (ClassNotFoundException e) {
					logger.warn("ClassNotFoundException：【{}】", name);
				}
			}
		}
		// 日志脱敏
		// 日志脱敏
		Set<String> clazzNameSet = PackageUtil.getClassSet("com.xx.model", true);
		clazzNameSet.addAll(PackageUtil.getClassSet("com.xx.dto", true));
		clazzNameSet.addAll(PackageUtil.getClassSet("com.xx.vo", true));
		clazzNameSet.addAll(PackageUtil.getClassSet("com.xx.vo", true));
		Set<Class<?>> clazzSet = clazzNameSet.stream().map(name -> {
			try {
				return Class.forName(name);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toSet());
		configDesensitizers(inConfig, clazzSet);
	}

	private static void configDesensitizers(SerializeConfig config, Set<Class<?>> clazzSet)
			throws ClassNotFoundException, IOException, URISyntaxException {
		Map<String, Desensitizer> desensitizers = new HashMap<>();
		desensitizers.put("certifyNo", StringExtUtil::overlayCertifyNo);
		desensitizers.put("legalCertifyNo", StringExtUtil::overlayCertifyNo);
		desensitizers.put("fullName", StringExtUtil::overlayUserNameCN);
		desensitizers.put("legalPerson", StringExtUtil::overlayUserNameCN);
		desensitizers.put("pwd", StringExtUtil::overlayPwd);
		desensitizers.put("mobilePhone", StringExtUtil::overlayMobileNo);
		desensitizers.put("legalMobileNo", StringExtUtil::overlayMobileNo);
		
		FastJsonLogUtil.desensitizers.putAll(desensitizers);
		clazzSet.stream().forEach(clazz -> {
			config.addFilter(clazz, desensitizerFilter);
		});
	}

	private static void setOutConfig() throws ClassNotFoundException, IOException, URISyntaxException {
		// 分页处理，集合处理。防止日志量爆炸
		ObjectSerializer listSerializer = new ObjectSerializer() {
			@Override
			public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
					throws IOException {
				List<?> list = (List<?>) object;
				serializer.write("list.size()=" + list.size());
			}
		};
		// 无法处理被嵌套的ArrayList
		outConfig.put(ArrayList.class, listSerializer);
		ObjectSerializer setSerializer = new ObjectSerializer() {
			@Override
			public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
					throws IOException {
				Set<?> set = (Set<?>) object;
				serializer.write("set.size()=" + set.size());
			}
		};
		outConfig.put(HashSet.class, setSerializer);
		// 日志脱敏
		Set<String> clazzNameSet = PackageUtil.getClassSet("com.xx.model", true);
		clazzNameSet.addAll(PackageUtil.getClassSet("com.xx.dto", true));
		clazzNameSet.addAll(PackageUtil.getClassSet("com.xx.vo", true));
		clazzNameSet.addAll(PackageUtil.getClassSet("com.xx.vo", true));
		Set<Class<?>> clazzSet = clazzNameSet.stream().map(name -> {
			try {
				return Class.forName(name);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toSet());
		configDesensitizers(outConfig, clazzSet);
		clazzSet.stream().forEach(clazz -> {
			outConfig.addFilter(clazz, collectionFilter);
		});
	}
}
