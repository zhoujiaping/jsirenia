package org.jsirenia.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class FileWatcher {
	public static void main(String[] args) throws IOException, InterruptedException {
		WatchService watcher = FileSystems.getDefault().newWatchService();
		String dir = "d:/";
		Path path = FileSystems.getDefault().getPath(dir);
		WatchKey key = path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
		while (true) {  
            key = watcher.take();  
            for (WatchEvent<?> event : key.pollEvents()) { 
                //获取目录下新增的文件名
                String fileName = event.context().toString();
                //检查文件名是否符合要求
                if("test.txt".equals(fileName)){
                    String filePath = path.toFile().getAbsolutePath()+File.separator+fileName;
                    System.out.println(new JFile(filePath).text());
                }
            }
            key.reset();  
        }  
	}
}
