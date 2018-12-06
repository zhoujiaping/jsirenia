package org.jsirenia.jdbc;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.jsirenia.util.runtime.RuntimeUtil;
/**
 * 用于做集成测试
 * 在测试类DemoTest中执行testDemo方法
 * try{
 * 	    scriptRunnerTestHelper.setup();
 * 	    ...
 * }finally{
 * 	    scriptRunnerTestHelper.teardown();
 *      ...
 * }
 * 将会分别执行DemoTest.class文件同目录下的
 * DemoTest-testDemo-setup.sql
 * DemoTest-testDemo-teardown.sql
 */
public class ScriptRunnerTestHelper {
	private ScriptRunnerHelper helper;
	private Class<?> klass;
	public ScriptRunnerTestHelper(DataSource ds,Class<?> klass){
		try {
			this.klass = klass;
			helper = new ScriptRunnerHelper()
			.withConnection(ds.getConnection())
			.withBaseDir(klass.getResource("./").getFile()).configRunner(r->{
				r.setAutoCommit(true);
			});
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public void setup(){
		String file = klass.getSimpleName()+"-"+getMethodName()+"-setup.sql";
		helper.runFileScript(file);
	}
	private String getMethodName(){
		String method = null;
		StackTraceElement[] eles = RuntimeUtil.stackTraceElements();
		for(int i=0;i<eles.length;i++){
			if(eles[i].getClassName().equals(klass.getName())){
				method = eles[i].getMethodName();
				break;
			}
		}
		return method;
	}
	public void teardown(){
		String file = klass.getSimpleName()+"-"+getMethodName()+"-teardown.sql";
		helper.runFileScript(file);
	}
}
