package org.jsirenia.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.jsirenia.bean.Beans;
import org.jsirenia.string.GenericTokenParser;
import org.jsirenia.util.Callback.Callback00;
import org.jsirenia.util.Callback.Callback01;
import org.jsirenia.util.Callback.Callback10;
import org.jsirenia.util.Callback.Callback11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DbClient {
	private static final Logger logger = LoggerFactory.getLogger(DbClient.class);
	private ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
	private Map<String, Savepoint> savePoints = new ConcurrentHashMap<>();
	/*
	 * 是否在withConn方法中。withTx需要判断，如果是在withConn方法中，
	 * 那么withTx就不能打开新的连接，只能使用原来的连接。也不能关闭连接。 如果是不是在withConn中运行，那么它需要关闭连接。
	 */
	private boolean inWithConn = false;
	private boolean inWithTx = false;
	private DataSource ds;

	public DbClient(DataSource ds) {
		this.ds = ds;
	}

	private Connection openConnection() throws SQLException {
		Connection conn = connectionHolder.get();
		if (conn != null) {
			throw new RuntimeException("当前线程已经有连接");
		}
		conn = ds.getConnection();
		conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
		if (conn.getAutoCommit()) {
			conn.setAutoCommit(false);
		}
		connectionHolder.set(conn);
		return conn;
	}

	/**
	 * 打开连接，执行回调，关闭连接
	 * 
	 * @param callback
	 */
	public void withConn(Callback10<Connection> callback) {
		withConn((conn) -> {
			callback.apply(conn);
			return null;
		});
	}

	/**
	 * 打开连接，执行回调，关闭连接
	 * 
	 * @param callback
	 */
	public <R> R withConn(Callback11<R, Connection> callback) {
		Connection conn = null;
		try {
			inWithConn = true;
			conn = openConnection();
			R res = callback.apply(conn);
			commit(conn);
			return res;
		} catch (Exception e) {
			rollback(conn);
			throw new RuntimeException(e);
		} finally {
			connectionHolder.remove();
			inWithConn = false;
			close(conn);
		}
	}

	private void commit(Connection conn) {
		if (conn != null) {
			try {
				if (!conn.isClosed() && !conn.getAutoCommit()) {
					conn.commit();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void close(Connection conn) {
		if (conn != null) {
			try {
				if (!conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void rollback(Connection conn) {
		if (conn != null) {
			try {
				if (!conn.isClosed() && !conn.getAutoCommit()) {
					conn.rollback();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void rollback(Connection conn, Savepoint point) {
		if (conn != null) {
			try {
				if (!conn.isClosed() && !conn.getAutoCommit() && point != null) {
					conn.rollback(point);
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void withTx(Callback00 callback) {
		withTx(() -> {
			callback.apply();
			return null;
		});
	}

	private <T> T withTxInternal(Callback01<T> callback) {
		Connection conn = null;
		String pointName = UUID.randomUUID().toString();
		try {
			inWithTx = true;
			conn = connectionHolder.get();
			Savepoint point = conn.setSavepoint(pointName);
			savePoints.put(pointName, point);
			T res = callback.apply();
			commit(conn);
			return res;
		} catch (Exception e) {
			Savepoint point = savePoints.remove(pointName);
			rollback(conn,point);
			throw new RuntimeException(e);
		} finally {
			savePoints.remove(pointName);
			inWithTx = false;
		}
	}

	public <T> T withTx(Callback01<T> callback) {
		if (!inWithConn) {
			return withConn(conn -> {
				return withTxInternal(callback);
			});
		}
		return withTxInternal(callback);
	}

	public JSONObject queryOne(String sql) {
		JSONArray array = query(sql, null);
		int size = array.size();
		if (size == 0) {
			return null;
		} else if (size > 1) {
			throw new RuntimeException("查询结果" + size + "条，预期1条");
		}
		return array.getJSONObject(0);
	}

	/**
	 * 执行查询。必须在withTx或者withConn方法的回调中执行
	 * 
	 * @param sql
	 * @return
	 */
	public JSONArray query(String sql) {
		return query(sql, null);
	}

	public JSONArray query(String sql, JSONObject params) {
		if (!inWithConn && !inWithTx) {
			String sql0 = sql;
			return withConn(conn -> {
				return queryInternal(sql0, params);
			});
		}
		return queryInternal(sql, params);
	}

	/**
	 * 执行查询，sql支持#{}和${}占位符功能。必须在withTx或者withConn方法的回调中执行
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	private JSONArray queryInternal(String sql, JSONObject params) {
		JSONObject parseRes = null;
		if (params != null) {
			// 设置参数
			parseRes = parseSql(sql, params);
			sql = parseRes.getString("sql");
		}
		logger.info("sql=>{}", sql);
		logger.info("params=>{}", params);
		Connection conn = getExistsConn();
		try (PreparedStatement ps = conn.prepareStatement(sql);) {
			if (params != null) {
				JSONArray names = parseRes.getJSONArray("names");
				for (int i = 0; i < names.size(); i++) {
					ps.setObject(i + 1, params.get(names.get(i)));
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
			logger.info("result size=>{}", list.size());
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}
	}

	private JSONObject parseSql(String sql, JSONObject params) {
		JSONObject res = new JSONObject();
		JSONArray names = new JSONArray();
		sql = new GenericTokenParser("#{", "}", (token) -> {
			names.add(token);
			return "?";
		}).parse(sql);
		sql = new GenericTokenParser("${", "}", (token) -> {
			return String.valueOf(params.get(token));
		}).parse(sql);
		res.put("sql", sql);
		res.put("names", names);
		return res;
	}
/*	public <T> T insert(String sql,JSONObject params){
			
	}*/
	/**
	 * 执行更新。必须在withTx或者withConn方法的回调中执行
	 * 
	 * @param sql
	 * @return
	 */
	public int update(String sql) {
		return update(sql, null);
	}

	public int update(String sql, JSONObject params) {
		if (!inWithConn && !inWithTx) {
			String sql0 = sql;
			return withConn(conn -> {
				return updateInternal(sql0, params);
			});
		}
		return updateInternal(sql, params);
	}

	public <T> T insert(String sql) {
		return insert(sql,null);
	}
	public <T> T insert(String sql, JSONObject params) {
		if (!inWithConn && !inWithTx) {
			String sql0 = sql;
			return withConn(conn -> {
				return insertInternal(sql0, params);
			});
		}
		return insertInternal(sql, params);
	}
	private <T> T insertInternal(String sql, JSONObject params) {
		JSONObject parseRes = null;
		if (params != null) {
			// 设置参数
			parseRes = parseSql(sql, params);
			sql = parseRes.getString("sql");
		}
		logger.info("sql=>{}", sql);
		logger.info("params=>{}", params);
		Connection conn = getExistsConn();
		try (PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
			if (params != null) {
				JSONArray names = parseRes.getJSONArray("names");
				for (int i = 0; i < names.size(); i++) {
					ps.setObject(i + 1, params.get(names.get(i)));
				}
			}
			ps.executeUpdate();
			ResultSet generatedKeys = ps.getGeneratedKeys();
			generatedKeys.next();
			@SuppressWarnings("unchecked")
			T id = (T) generatedKeys.getObject(1);
			if(id == null){
				throw new RuntimeException("自动生成主键为空");
			}
			return id;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 执行更新(自动提交)。必须在withTx或者withConn方法的回调中执行
	 * 
	 * @param sql
	 * @return
	 */
	private int updateInternal(String sql, JSONObject params) {
		JSONObject parseRes = null;
		if (params != null) {
			// 设置参数
			parseRes = parseSql(sql, params);
			sql = parseRes.getString("sql");
		}
		logger.info("sql=>{}", sql);
		logger.info("params=>{}", params);
		Connection conn = getExistsConn();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			if (params != null) {
				JSONArray names = parseRes.getJSONArray("names");
				for (int i = 0; i < names.size(); i++) {
					ps.setObject(i + 1, params.get(names.get(i)));
				}
			}
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	

	private Connection getExistsConn() {
		Connection conn = connectionHolder.get();
		try {
			if (conn == null || conn.isClosed()) {
				throw new RuntimeException("连接未获取或连接已关闭");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return conn;
	}
}
