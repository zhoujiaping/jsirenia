package org.jsirenia.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

public class ResourceUtil {
	public static String load(String path){
		DefaultResourceLoader loader = new DefaultResourceLoader();
		Resource r = loader.getResource(path);
		try (InputStream in = r.getInputStream();){
			String content = StreamUtils.copyToString(in,Charset.forName("utf-8"));
			return content;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static Properties loadProperties(String path){
		DefaultResourceLoader loader = new DefaultResourceLoader();
		Resource r = loader.getResource(path);
		Properties prop = new Properties();
		try (InputStream in = r.getInputStream();){
			prop.load(in);
			return prop;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
