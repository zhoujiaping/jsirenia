package org.jsirenia.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

public class JIndexRender {
	 private String openToken;
	  private String closeToken;
	  private NullValueStrategy nullValueStrategy  = NullValueStrategy.ThrowException;
	  private List<Object> values;
	public static enum NullValueStrategy{
		ThrowException,ReturnEmptyString,ReturnNullString
	}
	public JIndexRender(String openToken, String closeToken){
		 this.openToken = openToken;
		    this.closeToken = closeToken;
	}
	public JIndexRender(String openToken, String closeToken,NullValueStrategy nullValueStrategy){
		 this.openToken = openToken;
		    this.closeToken = closeToken;
		    this.nullValueStrategy = nullValueStrategy;
	}
	public JIndexRender withValues(Object... values){
		if(values==null){
			this.values = new ArrayList<>(0);
		}else{
			this.values = Arrays.asList(values);
		}
		return this;
	}
	public JIndexRender withValues(List<Object> values){
		this.values = values;
		return this;
	}
	public JIndexRender withValue(Object value){
		if(values==null){
			values = new ArrayList<>();
		}
		values.add(value);
		return this;
	}
	public String render(String text){
		return render(text,values);
	}
	public String render(String text,List<Object> values){
		Iterator<Object> iter = values.listIterator();
		GenericTokenParser tokenParser = new GenericTokenParser(openToken, closeToken, token->{
			Object v = iter.next();
			if(v==null ){
				switch(nullValueStrategy){
				case ThrowException:
					throw new RuntimeException("value of "+token+" is null");
				case ReturnEmptyString:
					return "";
				case ReturnNullString:
					return "null";
				default:
					throw new RuntimeException("this is imposible");
				}
			}else{
				return v.toString();
			}
		});
		return tokenParser.parse(text);
	}
	public static void main(String[] args) {
		String text = "i am {name},\r\nhello {yourname}";
		JIndexRender jrender = new JIndexRender("{", "}");
		String res = jrender.withValues("zhou","jiaping").render(text);
		System.out.println(res);
	}
}
