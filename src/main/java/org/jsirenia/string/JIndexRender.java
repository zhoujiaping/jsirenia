package org.jsirenia.string;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JIndexRender {
	private static final Logger logger = LoggerFactory.getLogger(JIndexRender.class);
	 private String openToken = "{";
	  private String closeToken = "}";
	  private NullValueStrategy nullValueStrategy  = NullValueStrategy.ThrowException;
	  private List<Object> values = new ArrayList<>();
	  private String text;
	public static enum NullValueStrategy{
		ThrowException,ReturnEmptyString,ReturnNullString
	}
	public JIndexRender clearValues() {
		this.values = new ArrayList<>();
		return this;
	}
	public JIndexRender(){
	}
	public JIndexRender withToken(String openToken, String closeToken){
		 this.openToken = openToken;
		    this.closeToken = closeToken;
		    return this;
	}
	public JIndexRender withNullValueStrategy(NullValueStrategy nullValueStrategy){
		    this.nullValueStrategy = nullValueStrategy;
		    return this;
	}
	public JIndexRender withValues(Object... values){
		if(values==null){
		}else{
			for(int i=0;i<values.length;i++) {
				this.values.add(values[i]);
			}
		}
		return this;
	}
	public JIndexRender withValues(List<Object> values){
		this.values = values;
		return this;
	}
	public JIndexRender withValue(Object value){
		values.add(value);
		return this;
	}
	public JIndexRender withText(String text){
		this.text = text;
		return this;
	}
	public String render(String text){
		return render(text,values);
	}
	public String render(String text,Object... values){
		return this.withValues(values).render(text);
	}
	public String render(){
		Iterator<Object> iter = values.listIterator();
		GenericTokenParser tokenParser = new GenericTokenParser(openToken, closeToken, token->{
			Object v = iter.next();
			if(v==null ){
				switch(nullValueStrategy){
				case ThrowException:
					throw new RuntimeException(render("value of {} is null",token));
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
		if(iter.hasNext()) {
			logger.warn("some values are not used!");
		}
		return tokenParser.parse(this.text);
	}

	public String render(String text,List<Object> values){
		return withText(text).withValues(values).render();
	}
	public static void main(String[] args) {
		String text = "i am {},\r\nhello {}";
		JIndexRender jrender = new JIndexRender();
		String res = jrender.withValues("zhou","jiaping").withValue("a").render(text);
		System.out.println(res);
	}
}
