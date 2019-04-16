package org.jsirenia.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jsirenia.bean.Beans;
import org.jsirenia.string.GenericTokenParser;
import org.jsirenia.util.Callback.Callback00;
import org.jsirenia.util.Callback.Callback10;
import org.jsirenia.util.Callback.Callback11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
public class DbClient {
	private static final Logger logger = LoggerFactory.getLogger(DbClient.class);
	private ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
	private DataSource ds;
	public DbClient(DataSource ds) {
		this.ds = ds;
	}
	private Connection getOrOpenConnection(boolean autoCommit) throws SQLException{
		Connection conn = connectionHolder.get();
		if(conn==null||conn.isClosed()){
			conn = ds.getConnection();
			connectionHolder.set(conn);
		}
		if(conn.getAutoCommit()!=autoCommit){
			conn.setAutoCommit(autoCommit);
		}
		return conn;
	}
	/**
	 * 打开连接，执行回调，关闭连接
	 * @param callback
	 */
	public void withConn(Callback10<Connection> callback) {
		withConn((conn)->{
			callback.apply(conn);
			return null;
		});
	}
	/**
	 * 打开连接，执行回调，关闭连接
	 * @param callback
	 */
	public <R>R withConn(Callback11<R,Connection> callback) {
		try (Connection conn =getOrOpenConnection(true)) {
			conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			// conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			return callback.apply(conn);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			connectionHolder.remove();
		}
	}
	public void withTx(Callback00 callback) {
		withTx((client)->{
			callback.apply();
		});
	}
	public void withTx(Callback10<DbClient> callback) {
		withTx((client)->{
			callback.apply(client);
			return null;
		});
	}
	public <T> T withTx(Callback11<T,DbClient> callback) {
		Connection conn = null;
		try {
			conn = getOrOpenConnection(false);
			T res = callback.apply(this);
			if(!conn.getAutoCommit()){
				conn.commit();
			}
			return res;
		} catch (Exception e) {
			try {
				if(conn!=null){
					conn.rollback();
				}
			} catch (SQLException e1) {
				throw new RuntimeException(e);
			}
			throw new RuntimeException(e);
		}
	}
	/**
	 * 执行查询。必须在withTx或者withConn方法的回调中执行
	 * @param sql
	 * @return
	 */
	public JSONArray query(String sql) {
		return query(sql,null);
	}
	/**
	 * 执行查询，sql支持#{}和${}占位符功能。必须在withTx或者withConn方法的回调中执行
	 * @param sql
	 * @param params
	 * @return
	 */
	public JSONArray query(String sql,JSONObject params) {
		JSONObject parseRes = null;
		if(params!=null){
			//设置参数
			parseRes = parseSql(sql,params);
			sql = parseRes.getString("sql");
		}
		logger.info("sql=>{}",sql);
		logger.info("params=>{}",params);
		Connection conn = connectionHolder.get();
		try {
			if(conn==null || conn.isClosed()){
				throw new RuntimeException("连接未获取或连接已关闭");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		try (PreparedStatement ps = conn.prepareStatement(sql);) {
			if(params!=null){
				JSONArray names = parseRes.getJSONArray("names");
				for(int i=0;i<names.size();i++){
					ps.setObject(i+1, params.get(names.get(i)));
				}
			}
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cc = rsmd.getColumnCount();
			JSONArray list = new JSONArray();
			while (rs.next()) {
				JSONObject map = new JSONObject();
				for (int i = 1; i <= cc; i++) {
					String label = rsmd.getColumnLabel(i);
					String key = Beans.underline2camel(label);
					Object value = rs.getObject(label);
					map.put(key, value);
				}
				list.add(map);
			}
			rs.close();
			logger.info("result size=>{}",list.size());
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
		}
	}

	private JSONObject parseSql(String sql, JSONObject params) {
		JSONObject res = new JSONObject();
		JSONArray names = new JSONArray();
		sql = new GenericTokenParser("#{","}",(token)->{
			names.add(token);
			return "?";
		}).parse(sql);
		sql = new GenericTokenParser("${","}",(token)->{
			return String.valueOf(params.get(token));
		}).parse(sql);
		res.put("sql", sql);
		res.put("names", names);
		return res;
	}
	/**
	 * 执行更新。必须在withTx或者withConn方法的回调中执行
	 * @param sql
	 * @return
	 */
	public int update(String sql) {
		return update(sql,null);
	}
	/**
	 * 执行更新(自动提交)。必须在withTx或者withConn方法的回调中执行
	 * @param sql
	 * @return
	 */
	public int update(String sql,JSONObject params) {
		JSONObject parseRes = null;
		if(params!=null){
			//设置参数
			parseRes = parseSql(sql,params);
			sql = parseRes.getString("sql");
		}
		logger.info("sql=>{}",sql);
		logger.info("params=>{}",params);
		Connection conn = connectionHolder.get();
		try {
			if(conn==null || conn.isClosed()){
				throw new RuntimeException("连接未获取或连接已关闭");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			if(params!=null){
				JSONArray names = parseRes.getJSONArray("names");
				for(int i=0;i<names.size();i++){
					ps.setObject(i+1, params.get(names.get(i)));
				}
			}
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}

