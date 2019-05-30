package org.jsirenia.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 */
public class XMLUtil {
	/**
	 * 读取xml文本的时候去掉注释。 dom解析太慢了，处理文本会快很多。
	 */
	public static String read(InputStream in) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("utf-8")))) {
			String line = null;
			boolean isFirstLine = true;
			StringBuilder text = new StringBuilder();
			while ((line = br.readLine()) != null) {
				if (isFirstLine) {
					text.append(line);
					isFirstLine = false;
				} else {
					text.append(System.lineSeparator());
					text.append(line);
				}
			}
			// 正则要使用懒惰模式，不能用默认的贪婪模式。(?s)使.*支持跨行匹配
			String xmltext = text.toString().replaceAll("(?s)<!--.*?-->", "");
			return xmltext;
		}
	}
}
