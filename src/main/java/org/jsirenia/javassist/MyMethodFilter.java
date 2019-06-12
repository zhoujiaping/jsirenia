package org.jsirenia.javassist;

import javassist.CtMethod;

public interface MyMethodFilter {
	boolean filter(CtMethod method);
}