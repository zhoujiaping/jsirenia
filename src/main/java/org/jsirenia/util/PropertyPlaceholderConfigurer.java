package org.jsirenia.util;

import java.io.IOException;
import java.util.Properties;

public class PropertyPlaceholderConfigurer extends org.springframework.beans.factory.config.PropertyPlaceholderConfigurer{
	private Properties props;

	public Properties mergeProperties() throws IOException {
		props = super.mergeProperties();
		String port = props.getProperty("HTTP_PORT");
		if(port!=null && port.trim().equals("auto")){
			port = ServerUtil.getHttpPort();
			if(port==null || port.trim().equals("")){
				throw new RuntimeException("自动获取http端口为空");
			}
			props.setProperty("HTTP_PORT", port);
		}
		return props;
	}
}
