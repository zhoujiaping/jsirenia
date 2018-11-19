package org.jsirenia.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.jsirenia.util.callback.Callback10;

public class JFile{
	private File file;
	public JFile(File file){
		this.file = file;
	}
	public JFile(String filename){
		this.file = new File(filename);
	}
	public void eachLine(Callback10<String> callback){
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))){
			String line = null;
			while((line = br.readLine())!=null){
				callback.apply(line);
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	public String text(){
		StringBuilder sb = new StringBuilder();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))){
			char[] cbuf = new char[1024];
			int len = -1;
			while((len = br.read(cbuf))>-1){
				sb.append(new String(cbuf,0,len));
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		return sb.toString();
	}
	public static void main(String[] args) {
		new JFile("f:/Settings.xml").eachLine(System.out::println);
	}
}
