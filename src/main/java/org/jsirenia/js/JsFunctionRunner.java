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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

/**
 * 重复加载文件的问题已经通过缓存解决。 增加了目录监听功能，如果文件发生修改，会在下一次执行该脚本的函数时，读取文件内容兵保存到缓存。
 * 重复eval脚本内容的问题已经通过缓存解决。 注意脚本的执行上下文，如果有多个脚本文件，后面eval的脚本变量（包括函数）会覆盖前面eval的脚本变量，
 * 所以需要开发者自己控制。
 * 
 * 其实脚本方案，这个有严重缺陷。通过脚本引擎执行js，js无法直接做文件io、网络io等。
 * https://www.cnblogs.com/qiumingcheng/p/7355456.html
 * https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/nashorn/toc.
 * html
 * 
 * js脚本中的load不会返回到java。所以不用那种方式。
 */
public class JsFunctionRunner {
	private static final Logger logger = LoggerFactory.getLogger(JsFunctionRunner.class);
	private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
	private static Invocable invocable = (Invocable) engine;
	// private static LRUCache<String, String> fileTextCache = new
	// LRUCache<>(1000);
	private static LRUCache<String, Object> jsObjectCache = new LRUCache<>(1000);
	private static LRUCache<String, String> fileChangeCache = new LRUCache<>(1000);// filename->""
	private static Lock lock = new ReentrantLock();
	private static long lockTime = 1000 * 10;
	private static TimeUnit timeUnit = TimeUnit.SECONDS;
	private static Object starter;

	/**
	 * 每一个js file经过eval之后都要返回一个js对象。
	 * 
	 */
	static {
		try {
			// Bindings bindings =
			// engine.getBindings(ScriptContext.ENGINE_SCOPE);
			// bindings.put("__root",
			// getKey(ResourceUtils.getFile("classpath:js")));

			File file = ResourceUtils.getFile("classpath:js/builtin/__starter.js");
			starter = evalFile(file);
			// readFileTextAndEval(ResourceUtils.getFile("classpath:js/builtin/http.js"));
			// readFileTextAndEval(ResourceUtils.getFile("classpath:js/builtin/file.js"));
			// readFileTextAndEval(ResourceUtils.getFile("classpath:js/builtin/mysql.js"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String readFileText(File file) {
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
	public static void watch(String dir) {
		Thread t = new Thread(() -> {
			try {
				watchInternal(dir);
			} catch (Exception e) {
				logger.error("监听js文件目录" + dir + "异常", e);
				throw new RuntimeException(e);
			}
		});
		t.setDaemon(true);
		t.start();
	}

	private static void watchInternal(String dir) {
		File dirFile;
		try {
			dirFile = ResourceUtils.getFile(dir);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		String dirFilePath = dirFile.getAbsolutePath();
		FileWatcher watcher = new FileWatcher().withDir(dirFilePath).withKind(StandardWatchEventKinds.ENTRY_MODIFY);
		watcher.watch(event -> {
			String filename = event.context().toString();
			File file = new File(dirFilePath, filename);
			// 優化:不要文件有修改就重新加载文件，而是先记录修改，用的时候才重新加载
			// String fileText = readFileText(file);
			String key = getKey(file);
			if (jsObjectCache.containsKey(key)) {// 只有在缓存中有的才需要标记为已改变
				fileChangeCache.put(key, "");
			}
			System.out.println(file.getName());
			// fileTextCache.put(file.getAbsolutePath(), fileText);
		});
	}

	private static String getKey(File file) {
		return file.getAbsolutePath();
	}

	public static void main(String[] args)
			throws InterruptedException, ScriptException, NoSuchMethodException, FileNotFoundException {
		watch("classpath:js");
		Object http = evalFile(ResourceUtils.getFile("classpath:js/builtin/http.js"));
		Object res = invokeMethod(http, "post", "http://www.baidu.com","");
		System.out.println(res);
	}

	public static Object evalFile(File file) {
		try {
			lock.tryLock(lockTime, timeUnit);
			String key = getKey(file);
			String fileText = null;
			if (fileChangeCache.containsKey(key)) {
				fileText = readFileText(file);
				fileChangeCache.remove(key);
				Object jsObject = engine.eval(fileText);
				jsObjectCache.put(key, jsObject);
				return jsObject;
			} else {
				Object jsObject = jsObjectCache.get(key);
				if (jsObject == null) {
					fileText = readFileText(file);
					jsObject = engine.eval(fileText);
					jsObjectCache.put(key, jsObject);
				}
				return jsObject;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	public static String invokeMethod(Object target, String method, Object... args) {
		try {
			lock.tryLock(lockTime, timeUnit);
			if (args == null) {
				args = new Object[0];
			}
			Object[] actualArgs = new Object[2 + args.length];
			actualArgs[0] = target;
			actualArgs[1] = method;
			for (int i = 2; i < actualArgs.length; i++) {
				actualArgs[i] = args[i - 2];
			}
			Object res = invocable.invokeMethod(starter, "__invoke", actualArgs);
			if (res == null) {
				return null;
			}
			return res.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}
}
