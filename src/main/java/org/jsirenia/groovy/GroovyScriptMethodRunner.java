package org.jsirenia.groovy;

import java.io.File;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.util.ResourceUtils;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

public class GroovyScriptMethodRunner {
	private GroovyClassLoader groovyClassLoader;
	public void initGroovyClassLoader() {
		CompilerConfiguration config = new CompilerConfiguration();
		config.setSourceEncoding("UTF-8");
		// 设置该GroovyClassLoader的父ClassLoader为当前线程的加载器(默认)
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		groovyClassLoader = new GroovyClassLoader(cl, config);
	}

	public GroovyObject loadGroovyScript(File file) throws Exception {
		Class<?> groovyClass = groovyClassLoader.parseClass(file);
		GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
		return groovyObject;
	}

	public Object invokeMethod(GroovyObject groovyObject, String method, Object args) throws Exception {
		Object res = groovyObject.invokeMethod(method, args);
		return res;
	}

	public static void main(String[] args) throws Exception {
		GroovyScriptMethodRunner runner = new GroovyScriptMethodRunner();
		runner.initGroovyClassLoader();
		GroovyObject go = runner.loadGroovyScript(ResourceUtils.getFile("classpath:groovy/Hello.groovy"));
		Object res = runner.invokeMethod(go, "hello", "lufy");
		System.out.println(res);
	}
}
