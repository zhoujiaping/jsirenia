package druid.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class CURDTest {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<String, Map<String, String>> cryptInfo = new HashMap<>();
	{
		Map<String, String> userCryptColumnMap = new HashMap<>();
		userCryptColumnMap.put("name", "username");
		cryptInfo.put("t_user", userCryptColumnMap);
	}

	@Test
	public void testInsert() {
		String sql = "insert into t_user(Name,age,create_time)values('test-name1',21,now());";
		sql = "insert into t_user(name,age,create_time)values('test-name1',21,now()),('test-name2',22,now());";
		SQLStatementParser parser = new SQLStatementParser(sql);
		SQLInsertStatement stmt = (SQLInsertStatement) parser.parseStatement();
		String tableName = stmt.getTableName().getSimpleName();
		List<String> columnNames = new ArrayList<>();
		Map<String, String> cryptColumns = cryptInfo.get(tableName);
		List<SQLExpr> columns = stmt.getColumns();
		for (SQLExpr column : columns) {
			if(column instanceof SQLName){
				SQLName columnExpr = (SQLName) column;
				String columnName = columnExpr.getSimpleName();
				columnNames.add(columnName);
			}
		}
		List<ValuesClause> valuesClauseList = stmt.getValuesList();
		for (ValuesClause valuesClause : valuesClauseList) {
			List<SQLExpr> valueExprList = valuesClause.getValues();
			int index = 0;
			for (SQLExpr valueExpr : valueExprList) {
				String columnName = columnNames.get(index);
				String cryptType = cryptColumns.get(columnName);
				if (cryptType != null) {
					if (valueExpr instanceof SQLCharExpr) {
						SQLCharExpr charExpr = (SQLCharExpr) valueExpr;
						String crypted = encrypt(charExpr.getText(), cryptType);
						charExpr.setText(crypted);
					}
				}
				index++;
			}
		}
		logger.info(stmt.toString());
	}

	@Test
	public void testUpdate() {
		String sql = "update t_user t set t.name='aaa' where t.name = 'test-1' and t.age>20 and create_time between '2019-06-01' and now()";
		//sql = "update t_user t set t.name='aaa' where t.name = 'test-1';";
		//sql = "update t_user t set t.name='aaa' where name='abc' and t.name not in( 'test-1','test-2') and 1=1 ;";
		SQLStatementParser parser = new SQLStatementParser(sql);
		SQLUpdateStatement stmt = (SQLUpdateStatement) parser.parseStatement();
		String tableName = stmt.getTableName().getSimpleName();
		Map<String, String> cryptColumns = cryptInfo.get(tableName);
		List<SQLUpdateSetItem> items = stmt.getItems();
		for(SQLUpdateSetItem item : items){
			SQLExpr column = item.getColumn();
			SQLExpr value = item.getValue();
			if(column instanceof SQLName && value instanceof SQLCharExpr){
				SQLName nameExpr = (SQLName) column;
				SQLCharExpr charExpr = (SQLCharExpr) value;
				encrypt(nameExpr,charExpr,cryptColumns);
			}
		}
		SQLExpr where = stmt.getWhere();
		cryptSQLExpr(where, tableName, cryptColumns);
		logger.info(stmt.toString());
	}
	@FunctionalInterface
	public interface ResultCryptCallback{
		JSONArray crypt(JSONArray records);
	}
	@Test
	public void testSelect() {
		String sql = "select * from t_user t where t.name = 'test-1' and t.age>20 and create_time between '2019-06-01' and now()";
		sql = "select * from t_user t where t.name = 'test-1';";
		sql = "select * from t_user t where name='abc' and t.name not in( 'test-1','test-2') and 1=1 ;";
		sql = "select t.name,t.age from t_user t;";
		SQLStatementParser parser = new SQLStatementParser(sql);
		SQLSelectStatement stmt = (SQLSelectStatement) parser.parseStatement();
		SQLSelect select = stmt.getSelect();
		SQLSelectQueryBlock queryBlock = select.getQueryBlock();
		SQLTableSource tableSource = queryBlock.getFrom();
		//TODO 表信息：表的所有columns
		Map<String,String> allColumnsType = new HashMap<>();
		allColumnsType.put("name", "varchar");
		//
		Set<String> needCryptTypes = new HashSet<>();
		needCryptTypes.add("varchar");
		Set<String> toCryptColumns = new HashSet<>();
		if(tableSource instanceof SQLExprTableSource){
			SQLExprTableSource exprTableSource = (SQLExprTableSource) tableSource;
			SQLName sqlName = exprTableSource.getName();
			String tableName = sqlName.getSimpleName();
			Map<String, String> cryptColumns = cryptInfo.get(tableName);
			List<SQLSelectItem> selectList = queryBlock.getSelectList();
			for(SQLSelectItem item : selectList){
				SQLExpr expr = item.getExpr();
				if(expr instanceof SQLAllColumnExpr){
					allColumnsType.forEach((columnName,columnType)->{
						if(needCryptTypes.contains(columnType)){
							toCryptColumns.add(columnName);
						}
					});
				}else if(expr instanceof SQLName){
					SQLName name = (SQLName) expr;
					String columnName = name.getSimpleName();
					String columnType = allColumnsType.get(columnName);
					if(needCryptTypes.contains(columnType)){
						toCryptColumns.add(columnName);
					}
				}
			}
			SQLExpr where = queryBlock.getWhere();
			cryptSQLExpr(where, tableName, cryptColumns);
			//执行sql，得到结果（结果为JSONArray类型）
			JSONArray records = new JSONArray();
			JSONObject record = new JSONObject();
			record.put("name", "天天");
			record.put("age", 20);
			records.add(record );
			
			ResultCryptCallback callback = new ResultCryptCallback(){
				@Override
				public JSONArray crypt(JSONArray records) {
					for(Object record : records){
						JSONObject json = (JSONObject) record;
						Set<Entry<String,Object>> entrySet = json.entrySet();
						for(Entry<String,Object> entry : entrySet){
							String key = entry.getKey();
							if(toCryptColumns.contains(key)){
								Object value = entry.getValue();
								String cryptType = cryptColumns.get(key);
								if(value != null){
									value = decrypt(value.toString(), cryptType);
									entry.setValue(value);
									logger.info("解密"+key);
								}
							}
						}
						json.forEach((name,value)->{
							if(toCryptColumns.contains(name)){
								
							}
						});
					}
					return null;
				}
			};
			callback.crypt(records);
		}
		SQLLimit limit = queryBlock.getLimit();
		if(limit == null){
			//防止查全表
			limit = new SQLLimit();
			limit.setOffset(0);
			limit.setRowCount(20);
			queryBlock.setLimit(limit);
		}
		logger.info(stmt.toString());
	}

	@Test
	public void testDelete() {
		String sql = "delete from t_user t where t.name = 'test-1' and t.age>20 and create_time between '2019-06-01' and now()";
		sql = "delete from t_user t where t.name = 'test-1';";
		sql = "delete from t_user t where name='abc' and t.name not in( 'test-1','test-2') and 1=1 ;";
		sql = "delete from t_user t;";
		SQLStatementParser parser = new SQLStatementParser(sql);
		SQLDeleteStatement stmt = (SQLDeleteStatement) parser.parseStatement();
		String tableName = stmt.getTableName().getSimpleName();
		Map<String, String> cryptColumns = cryptInfo.get(tableName);
		SQLExpr where = stmt.getWhere();
		cryptSQLExpr(where, tableName, cryptColumns);
		logger.info(stmt.toString());
	}

	private void cryptSQLExpr(SQLExpr expr, String tableName, Map<String, String> cryptColumns) {
		if (expr instanceof SQLBinaryOpExpr) {
			SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) expr;
			SQLExpr left = binaryOpExpr.getLeft();
			SQLExpr right = binaryOpExpr.getRight();
			if (left instanceof SQLName) {//SQLPropertyExpr或者SQLIdentifierExpr
				if (right instanceof SQLCharExpr) {
					SQLName propExpr = (SQLName) left;
					SQLCharExpr charExpr = (SQLCharExpr) right;
					String columnName = propExpr.getSimpleName();
					String cryptType = cryptColumns.get(columnName);
					String crypted = encrypt(charExpr.getText(), cryptType);
					charExpr.setText(crypted);
				}
			}
			cryptSQLExpr(left, tableName, cryptColumns);
			cryptSQLExpr(right, tableName, cryptColumns);
		} else if (expr instanceof SQLInListExpr) {
			SQLInListExpr inListExpr = (SQLInListExpr) expr;
			SQLExpr inExpr = inListExpr.getExpr();
			if (inExpr instanceof SQLName) {
				SQLName propExpr = (SQLName) inExpr;
				List<SQLExpr> targetList = inListExpr.getTargetList();
				String columnName = propExpr.getSimpleName();
				String cryptType = cryptColumns.get(columnName);
				for (SQLExpr target : targetList) {
					if (target instanceof SQLCharExpr) {
						SQLCharExpr charExpr = (SQLCharExpr) target;
						String crypted = encrypt(charExpr.getText(), cryptType);
						charExpr.setText(crypted);
					}
				}
			}
		}
	}
	private void encrypt(SQLName sqlName,SQLCharExpr charExpr,Map<String,String> cryptColumns){
		String columnName = sqlName.getSimpleName();
		String cryptType = cryptColumns.get(columnName);
		String crypted = encrypt(charExpr.getText(), cryptType);
		charExpr.setText(crypted);
	}
	private String encrypt(String value, String cryptType) {
		if (cryptType == null) {
			return value;
		}
		return value + "->加密（" + cryptType + "）";
	}

	private String decrypt(String value, String cryptType) {
		if (cryptType == null) {
			return value;
		}
		return value + "->解密（" + cryptType + "）";
	}
}
