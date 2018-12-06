package org.jsirenia.jdbc;


import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jsirenia.file.PathUtil;
import org.jsirenia.util.callback.Callback10;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
/**
 * 可以用于运行一个sql文件，测试的时候方便初始化数据。
 * @author 01375156 
 */
public class ScriptRunnerHelper {
	private Connection connection;
	private ScriptRunner runner;
	private String charset = "utf-8";
	private String baseDir;
	public ScriptRunnerHelper withConnection(Connection connection){
		this.connection = connection;
		runner = new ScriptRunner(connection);
		runner.setSendFullScript(false);
		return this;
	}
	public ScriptRunnerHelper withCharset(String charset){
		this.charset = charset;
		return this;
	}
	public ScriptRunnerHelper withBaseDir(String baseDir){
		this.baseDir = baseDir;
		return this;
	}
	public ScriptRunnerHelper configRunner(Callback10<ScriptRunner> fun){
		fun.apply(runner);
		return this;
	}
	public ScriptRunnerHelper runFileScript(String uri){
		try{
			if(baseDir!=null){
				uri = PathUtil.concat(baseDir, uri);
			}
			File file = ResourceUtils.getFile(uri);
			byte[] buf = FileCopyUtils.copyToByteArray(file);
			Reader reader = new StringReader(new String(buf,this.charset));
			runner.runScript(reader );
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	public ScriptRunnerHelper runScript(String text){
		try{
			Reader reader = new StringReader(text);
			runner.runScript(reader );
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	public void detroy(){
		try {
			runner=null;
			connection.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public String getCharset() {
		return charset;
	}
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8";
		String driver = "com.mysql.jdbc.Driver";
		String username = "root";
		String password = "";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, username, password);
		String text = "show tables;111\nselect * from\n sys_user;\nselect * from sys_user;";
		ScriptRunnerHelper helper = new ScriptRunnerHelper()
				.withConnection(conn)
				.configRunner(runner->{
					runner.setSendFullScript(false);
				})
				.runScript(text );
		helper.detroy();
	}
}
