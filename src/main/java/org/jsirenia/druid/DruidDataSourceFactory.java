package org.jsirenia.druid;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.DataSourceFactory;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 用来配置mybatis的configuration.xml
 * 			<dataSource type="xxx.DruidDataSourceFactory">
 */
public class DruidDataSourceFactory implements DataSourceFactory{
	private volatile DruidDataSource ds;
	private Properties props;
	@Override
	public void setProperties(Properties props) {
		this.props = props;		
	}

	@Override
	public DataSource getDataSource() {
		if(ds == null){
			synchronized (this) {
				if(ds == null){
					ds = initDruidDataSource();
				}
			}
		}
		return ds;
	}

	private DruidDataSource initDruidDataSource() {
		DruidDataSource ds = new DruidDataSource();
		ds.setConnectProperties(props);
		return ds;
	}

}
