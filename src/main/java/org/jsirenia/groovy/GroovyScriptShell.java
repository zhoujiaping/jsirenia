package org.jsirenia.groovy;

import java.io.File;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class GroovyScriptShell {
	public Object evaluate(File file, Binding binding) {
		// Binding binding = new Binding();
		// binding.setProperty("name", "lufy");
		GroovyShell groovyShell = new GroovyShell(binding);
		Object result;
		try {
			result = groovyShell.evaluate(file);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Object evaluate(String script, Binding binding) {
		GroovyShell groovyShell = new GroovyShell(binding);
		Object result;
		try {
			result = groovyShell.evaluate(script);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
