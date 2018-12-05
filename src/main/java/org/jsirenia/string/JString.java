package org.jsirenia.string;

public class JString {
	public static String render(String text,Object... args){
		return new JIndexRender().withText(text).withValues(args).render();
	}
}
