package org.jsirenia.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.jsirenia.util.callback.Callback10;
/**
 * 封装文件操作
 */
public class JFile{
	private File file;
	public JFile(File file){
		this.file = file;
	}
	public JFile(String filename){
		this.file = new File(filename);
	}
	public File getFile(){
		return file;
	}
	/**
	 * 递归删除该文件及其子文件
	 */
	public void deleteRecursion(){
		walkRecursion(f->f.delete());
	}
	/**
	 * 遍历其子文件和当前文件
	 */
	public void walk(Callback10<File> cb) {
		File[] files = file.listFiles();
		for(int i=0;i<files.length;i++){
			cb.apply(files[i]);
		}
		cb.apply(file);
	}
	/**
	 * 遍历文件（深度优先，即优先处理子孙文件）
	 */
	public void walkRecursion(Callback10<File> cb) {
		File[] files = file.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].isDirectory()){
				walkRecursion(cb);
			}
			cb.apply(files[i]);
		}
		cb.apply(file);
	}
	/**
	 * 遍历当前文件的文本行
	 */
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
	/**
	 * 获取当前文件的文本内容
	 */
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
