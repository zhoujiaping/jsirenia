package org.jsirenia.bean;

public class PathUtil {
	public static String concat(String path0,String... paths){
		//Pattern p = Pattern.compile("/", Pattern.MULTILINE);
		StringBuilder path = new StringBuilder(path0);
		if(paths!=null){
			for(int i=0;i<paths.length;i++){
				path.append(paths[i]);
			}
		}
		String res = path.toString().replaceAll("[/\\\\]+", "/");
		return res;
	}
	public static void main(String[] args) {
		String host = "localhost";
		String port = "8080";
		String rootPath = "/a/b/\\/";
		String serviceName = "/cc";
		String serviceUrl = "http://"+PathUtil.concat(host, ":",port+"",rootPath,serviceName);
		System.out.println(serviceUrl);//result=>http://localhost:8080/a/b/cc
	}
}