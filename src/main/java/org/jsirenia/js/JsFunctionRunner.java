package org.jsirenia.js;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.StandardWatchEventKinds;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jsirenia.cache.LRUCache;
import org.jsirenia.file.FileWatcher;
import org.jsirenia.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**
 * 重复加载文件的问题已经通过缓存解决。
 * 增加了目录监听功能，如果文件发生修改，会在下一次执行该脚本的函数时，读取文件内容兵保存到缓存。
 * 重复eval脚本内容的问题已经通过缓存解决。
 * 注意脚本的执行上下文，如果有多个脚本文件，后面eval的脚本变量（包括函数）会覆盖前面eval的脚本变量，
 * 所以需要开发者自己控制。
 */
public class JsFunctionRunner {
	private static final Logger logger = LoggerFactory.getLogger(JsFunctionRunner.class);
	private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
	private static Invocable invocable = (Invocable) engine;
	private static LRUCache<String, String> fileTextCache = new LRUCache<>(1000);
	private static LRUCache<String,String> fileChangeCache = new LRUCache<>(1000);//filename->""
	private static Lock lock = new ReentrantLock();
	private static long lockTime = 1000*10;
	private static TimeUnit timeUnit = TimeUnit.SECONDS;

	/**
	 * 将给定文件的内容作为js代码执行，并且将字符串作为入参和出参。
	 * @param file
	 * @param arg
	 * @return
	 */
	public static String runFile(File file,String funcName, String arg) {
		try{
			lock.tryLock(lockTime, timeUnit);
			String fileText = readFileTextAndEval(file);
			return run(fileText, funcName, arg);
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally {
			lock.unlock();
		}
	}
	private static String readFileTextAndEval(File file) throws ScriptException{
		String key = file.getAbsolutePath();
		String fileText = null;
		if(fileChangeCache.containsKey(key)){
			fileText = readFileTextActually(file);
			fileChangeCache.remove(key);
			fileTextCache.put(key, fileText);
			engine.eval(fileText);
		}else{
			fileText = fileTextCache.get(key);
			if(fileText==null){
				fileText = readFileTextActually(file);
				fileTextCache.put(key, fileText);
				engine.eval(fileText);
			}
		}
		return fileText;
	}
	private static String readFileTextActually(File file){
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append(System.lineSeparator());
			}
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	private static String readFileText(String file){
		File f = null;
		try {
			f = ResourceUtils.getFile(file);
			return readFileTextAndEval(f);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 将给定名称的文件的内容作为js代码执行，并且将字符串作为入参和出参。
	 * @param file
	 * @param arg
	 * @return
	 */
	public static String runFile(String fileName,String funcName, String arg) {
		try{
			lock.tryLock(lockTime, timeUnit);
			//String fileText = readFileText(fileName);
			File file = ResourceUtils.getFile(fileName);
			return runFile(file,funcName, arg);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 执行给定的js代码，并且将字符串作为入参和出参。
	 * 字符串是最通用的方式，所以设计成入参和出参都是字符串。
	 * @param fileText
	 * @param arg
	 * @return
	 */
	public static String run(String fileText,String funcName, String arg) {
		try {
			lock.tryLock(lockTime, timeUnit);
			//engine.eval(fileText);
			return runFunction(funcName,arg);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			lock.unlock();
		}
	}
	private static String runFunction(String funcName,String arg) throws NoSuchMethodException, ScriptException{
		Object res;
		Object param = null;
		if (arg != null) {
			if(JSONUtil.isJSONArray(arg)){
				param = JSONArray.parseArray(arg);
			}else{
				param = JSONObject.parseObject(arg);
			}
		}
		res = invocable.invokeFunction(funcName, param);
		if (res == null) {
			return null;
		}
		return res.toString();
	}
	public static void watch(String dir){
		Thread t = new Thread(()->{
			try{
				watchInternal(dir);
			}catch(Exception e){
				logger.error("监听js文件目录"+dir+"异常",e);
				throw new RuntimeException(e);
			}
		});
		t.setDaemon(true);
		t.start();
	}
	private static void watchInternal(String dir){
		File dirFile;
		try {
			dirFile = ResourceUtils.getFile(dir);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		String dirFilePath = dirFile.getAbsolutePath();
		FileWatcher watcher = new FileWatcher().withDir(dirFilePath).withKind(StandardWatchEventKinds.ENTRY_MODIFY);
		watcher.watch(event->{
			String filename = event.context().toString();
			File file = new File(dirFilePath,filename);
			//優化:不要文件有修改就重新加载文件，而是先记录修改，用的时候才重新加载
			//String fileText = readFileText(file);
			String key = file.getAbsolutePath();
			if(fileTextCache.containsKey(key)){//只有在缓存中有的才需要标记为已改变
				fileChangeCache.put(key, "");
			}
			System.out.println(file.getName());
			//fileTextCache.put(file.getAbsolutePath(), fileText);
		});
	}
	public static void main(String[] args) throws InterruptedException {
		watch("classpath:js");
		test1();
		test2();
		test3();
	}
	private static void test3() {
		JSONObject params = new JSONObject();
		params.put("name", "zhou");
		String res = JsFunctionRunner.runFile("classpath:js/test.js", "x", params.toJSONString() );
		System.out.println(res);
	}
	private static void test1() {
		JSONObject params = new JSONObject();
		params.put("name", "zhou");
		String res = JsFunctionRunner.runFile("classpath:js/test.js", "test", params.toJSONString() );
		System.out.println(res);
	}
	private static void test2() {
		JSONObject params = new JSONObject();
		params.put("name", "zhou");
		String res = JsFunctionRunner.runFile("classpath:js/test2.js", "test2", params.toJSONString() );
		System.out.println(res);
	}
}
