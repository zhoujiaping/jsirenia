package org.jsirenia.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Js {
	private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
	private static Invocable invocable = (Invocable) engine;

	/**
	 * 测试java传参到js文件，并且js将返回结果返回给java。
	 * 
	 * @throws FileNotFoundException
	 */
	//@Test
	public void testArg() throws FileNotFoundException {
		JSONObject json = new JSONObject();
		json.put("name", "avril lavigne");
		json.put("age", 110);
		json.put("like", 3.14);
		String arg = json.toJSONString();
		/**
		 * 使用spring的ResourceUtils，方便读取文件
		 */
		File file = ResourceUtils.getFile("classpath:js/testArg.js");
		String res = run(file,"main", arg);
		System.out.println(res);
	}

	/**
	 * 将给定文件的内容作为js代码执行，并且将字符串作为入参和出参。
	 * @param file
	 * @param arg
	 * @return
	 */
	public static String run(File file,String funcName, String arg) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append(System.lineSeparator());
			}
			return run(sb.toString(),funcName, arg);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 将给定名称的文件的内容作为js代码执行，并且将字符串作为入参和出参。
	 * @param file
	 * @param arg
	 * @return
	 */
	public static String runFile(String file,String funcName, String arg) {
		File f;
		try {
			f = ResourceUtils.getFile(file);
			return run(f,funcName, arg);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 执行给定的js代码，并且将字符串作为入参和出参。
	 * 字符串是最通用的方式，所以设计成入参和出参都是字符串。
	 * @param js
	 * @param arg
	 * @return
	 */
	public static String run(String js,String funcName, String arg) {
		Object res;
		try {
			Object param = null;
			if (arg != null) {
				try{
					param = JSONObject.parseObject(arg);
				}catch(Exception e){
					param = JSONArray.parseArray(arg);
				}
			}
			engine.eval(js);
			res = invocable.invokeFunction(funcName, param);
			if (res == null) {
				return null;
			}
			return res.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
