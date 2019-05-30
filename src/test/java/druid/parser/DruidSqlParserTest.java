package druid.parser;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.springframework.util.ResourceUtils;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlShowColumnOutpuVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sfpay.jyd.util.Jfile;
/**
 * https://github.com/alibaba/druid/wiki/SQL-Parser
 * https://www.jianshu.com/p/437aa22ea3ca
 * @author 01375156
 *
 */
public class DruidSqlParserTest {
	@Test
	public void test() throws Exception {
		// 从文件中读取多条sql，解析每一条sql，将每条sql解析的结果存入文件，后续分析
		Jfile file = new Jfile(ResourceUtils.getFile("classpath:druid/test.sql"));
		String content = file.text();
		String[] sqls = content.trim().split(";");
		int i = 0;
		for (String sql : sqls) {
			MySqlStatementParser parser = new MySqlStatementParser(sql);
			SQLStatement statement = parser.parseStatement();
			File f = new File("d:", "/ast/ast-" + leftPad(i, "0", 2) + ".txt");
			Jfile resFile = new Jfile(f);
			String text = sql + "\r\n";
			text += JSON.toJSONString(statement, SerializerFeature.WriteClassName, SerializerFeature.PrettyFormat);
			resFile.writeText(text);
			i++;
		}
		// SQLSelect
		// SQLExprTableSource 表
	}

	public static String leftPad(Object o, String fill, int len) {
		if (fill == null || fill.length() == 0) {
			throw new RuntimeException("填充的字符串不能为空");
		}
		String v = String.valueOf(o);
		int padLen = len - v.length();
		int fillLen = fill.length();
		while (padLen >= fillLen) {
			v = fill + v;
			padLen -= fillLen;
		}
		return v;
	}

	@Test
	public void testSelect() throws Exception {
		String sql = "select * from t_iou t1 left join (	select * from t_cust where valid_flag = 1) t2 on t1.cust_id = t2.id where t1.valid_flag = 1 limit 10;";

		final String dbType = JdbcConstants.MYSQL; // 可以是ORACLE、POSTGRESQL、SQLSERVER、ODPS等
		List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
		
		//MySqlStatementParser parser = new MySqlStatementParser(sql);
		SQLStatement statement = stmtList.get(0);
		SQLSelectStatement stmt = (SQLSelectStatement) statement;
		SQLSelect select = stmt.getSelect();
		SQLASTVisitor visitor = new MySqlASTVisitorAdapter(){
			
		};
		
		SQLASTVisitor visitorProxy = (SQLASTVisitor) Proxy.newProxyInstance(visitor.getClass().getClassLoader(), new Class[]{SQLASTVisitor.class}, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				for(Object arg : args){
					System.out.print(arg);
				}
				System.out.println();
				System.out.println("===============");
				return method.invoke(visitor, args);
			}
		});
		List<Object> params = new LinkedList<>();
		Appendable sb = new StringBuilder();
		MySqlExportParameterVisitor mySqlExportParameterVisitor = new MySqlExportParameterVisitor(params,sb,true){
		};
		MySqlShowColumnOutpuVisitor mySqlShowColumnOutpuVisitor = new MySqlShowColumnOutpuVisitor(sb);
		
		MySqlSchemaStatVisitor mySqlSchemaStatVisitor = new MySqlSchemaStatVisitor();
		SQLASTVisitor v = mySqlSchemaStatVisitor;
		MySqlASTVisitor mysqlVisitor = new MySqlASTVisitorAdapter(){
			
		};
		select.accept(v);
		select.toString();
	}
}
