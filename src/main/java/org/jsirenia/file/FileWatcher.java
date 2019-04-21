package org.jsirenia.file;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import org.jsirenia.util.Callback;

/**
 * @author zjp
 */
public class FileWatcher {
	private String dir;
	private List<Kind<Path>> kindList = new ArrayList<>();
	private boolean enabled = true;
	public FileWatcher withDir(String dir){
		this.dir = dir;
		return this;
	}
	/**
	 * 参考StandardWatchEventKinds
	 * @param kind 监听类型
	 * @return FileWatcher
	 */
	public FileWatcher withKind(Kind<Path> kind){
		kindList.add(kind);
		return this;
	}
	public void stop(){
		enabled = false;
	}
	/**
	 * 参考WatchEvent
	 * @param cb 监听到事件的回调
	 */
	public void watch(Callback.Callback10<WatchEvent<?>> cb){
		try{
			watchInternal(cb);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	private void watchInternal(Callback.Callback10<WatchEvent<?>> cb) throws IOException, InterruptedException{
		WatchService watcher = FileSystems.getDefault().newWatchService();
		Path path = FileSystems.getDefault().getPath(dir);
		path.register(watcher, kindList.toArray(new Kind[0]));
		while (enabled) {
			WatchKey key = watcher.take();
            for (WatchEvent<?> event : key.pollEvents()) { 
            	cb.apply(event);
            }
            key.reset();  
        }  
	}
	public static void main(String[] args){
		String dir = "f:/";
		new FileWatcher().withDir(dir).withKind(StandardWatchEventKinds.ENTRY_MODIFY)
		.watch(event->{
			//获取目录下新增的文件名
            String fileName = event.context().toString();
            //检查文件名是否符合要求
			String ignoredFileName = "text.txt";
            if(ignoredFileName.equals(fileName)){
                String filePath = PathUtil.concat(dir, fileName);
                System.out.println(new MyFile(filePath).readText());
            }
		});
	}
}
