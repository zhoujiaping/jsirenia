package org.jsirenia.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

public class ResourceUtil {
	public static final LineHandler C_LANG_LINE_HANDLER = new LineHandler() {
		@Override
		public String handle(String line) {
			/*TODO 有可能包含字符串 http://localhost:8080/...  这样会导致bug
			 * 所以权衡之后，并不支持所有的注释，只支持空白符之后的//
			 * */
			return line.replaceAll("//.*$", "");
		}
	};
	public static final LineHandler XML_LANG_LINE_HANDLER = new LineHandler() {
		@Override
		public String handle(String line) {
			return line;
		}
	};;
	public static final ContentHandler C_LANG_CONTENT_HANDLER = new ContentHandler() {
		@Override
		public String handle(String content) {
			return content.replaceAll("(?s)/\\*.*?\\*/", "");// 正则要使用懒惰模式，不能用默认的贪婪模式。
		}
	};
	public static final ContentHandler XML_LANG_CONTENT_HANDLER = new ContentHandler() {
		@Override
		public String handle(String content) {
			return content.replaceAll("(?s)<!--.*?-->", "");
		}
	};
	private static Map<String, LineHandler> lineHandlerMap = new ConcurrentHashMap<>();
	private static Map<String, ContentHandler> contentHandlerMap = new ConcurrentHashMap<>();

	static {
		registryLang("js",C_LANG_LINE_HANDLER,C_LANG_CONTENT_HANDLER);
		registryLang("json",C_LANG_LINE_HANDLER,C_LANG_CONTENT_HANDLER);
		registryLang("java",C_LANG_LINE_HANDLER,C_LANG_CONTENT_HANDLER);
		registryLang("c",C_LANG_LINE_HANDLER,C_LANG_CONTENT_HANDLER);
		registryLang("cpp",C_LANG_LINE_HANDLER,C_LANG_CONTENT_HANDLER);
		registryLang("xml",XML_LANG_LINE_HANDLER,XML_LANG_CONTENT_HANDLER);
		registryLang("html",XML_LANG_LINE_HANDLER,XML_LANG_CONTENT_HANDLER);
	}

	public static boolean registryLang(String lang, LineHandler lineHandler, ContentHandler contentHandler) {
		if (lineHandlerMap.containsKey(lang)) {
			return false;
		}
		contentHandlerMap.put(lang, contentHandler);
		lineHandlerMap.put(lang, lineHandler);
		return true;
	}

	public static String loadCode(String path, String codeLang) {
		return null;
	}

	public static String load(String path, LineHandler lineHandler, ContentHandler contentHandler) throws IOException {
		DefaultResourceLoader loader = new DefaultResourceLoader();
		Resource r = loader.getResource(path);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(r.getInputStream(), "utf-8"));) {
			String line = null;
			boolean isFirstLine = true;
			StringBuilder text = new StringBuilder();
			while ((line = br.readLine()) != null) {
				if (lineHandler != null) {
					line = lineHandler.handle(line);
				}
				if (isFirstLine) {
					text.append(line);
					isFirstLine = false;
				} else {
					text.append(System.lineSeparator());
					text.append(line);
				}
			}
			String content = text.toString();
			if (contentHandler != null) {
				content = contentHandler.handle(content);
			}
			return content;
		}
	}

	public static String load(String path) {
		DefaultResourceLoader loader = new DefaultResourceLoader();
		Resource r = loader.getResource(path);
		try (InputStream in = r.getInputStream();) {
			String content = StreamUtils.copyToString(in, Charset.forName("utf-8"));
			return content;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Properties loadProperties(String path) {
		DefaultResourceLoader loader = new DefaultResourceLoader();
		Resource r = loader.getResource(path);
		Properties prop = new Properties();
		try (InputStream in = r.getInputStream();) {
			prop.load(in);
			return prop;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public interface LineHandler {
		String handle(String line);
	}

	public interface ContentHandler {
		String handle(String content);
	}
}
