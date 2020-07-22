package org.jsirenia.ftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 */
public class SftpClient implements Closeable {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ChannelSftp channel;

    public SftpClient(ChannelSftp channel) {
        this.channel = channel;
    }

    /**
     * 上传文件, 将输入流上传为sftp指定文件
     *
     * @param fromStream 输入流
     * @param toFile     上传为sftp的目标文件, 如:/upload/test/aaaa.txt
     */
    public void uploadAsFile(InputStream fromStream, String toFile) throws SftpException {
        if (fromStream == null) {
            throw new IllegalArgumentException("fromStream can't be null");
        }
        if (toFile == null) {
            throw new IllegalArgumentException("toFile can't be null");
        }
        toFile = winPathToLinuxPath(toFile);
        // 创建目标文件夹
        mkdirs(Paths.get(toFile).getParent().toString());
        channel.put(fromStream, toFile);
    }

    /**
     * 下载文件
     *
     * @param fromFile 要下载的文件路径
     */
    public InputStream download(String fromFile) throws SftpException {
        if (fromFile == null) {
            throw new IllegalArgumentException("fromFile can't be null");
        }
        fromFile = winPathToLinuxPath(fromFile);
        return channel.get(fromFile);
    }

    /**
     * 下载文件
     * 流关闭一般原则：谁打开谁关闭，所以这里不关闭流
     * @param fromFile 要下载的文件路径
     */
    public void download(String fromFile, OutputStream outStream) throws SftpException {
        if (outStream == null) {
            throw new IllegalArgumentException("outStream can't be null");
        }
        if (fromFile == null) {
            throw new IllegalArgumentException("fromFile can't be null");
        }
        fromFile = winPathToLinuxPath(fromFile);
        channel.get(fromFile, outStream);
    }

    /**
     * 相当于 rm -r targetFile
     * 一般不会有这种操作，慎用！！！
     * 一般不会有这种操作，慎用！！！
     * 一般不会有这种操作，慎用！！！
     *
     * @param targetDir ftp目录
     */
    public void deleteDirRecursive(String targetDir) throws SftpException {
        //是否需要判断targetDir为/?一般没有权限的
        if (targetDir == null) {
            throw new IllegalArgumentException("targetDir can't be null");
        }
        if (targetDir.equals("/")) {
            throw new IllegalArgumentException("targetDir can't be '/'!!! what are you doing???");
        }
        targetDir = winPathToLinuxPath(targetDir);
        if (existsDir(targetDir)) {
            Vector<LsEntry> files = channel.ls(targetDir);
            // 先删除目录下的文件
            Iterator<LsEntry> fileIterator = files.iterator();
            while (fileIterator.hasNext()) {
                LsEntry lsEntry = fileIterator.next();
                String fileName = lsEntry.getFilename();
                if (!".".equals(fileName) && !"..".equals(fileName)) {
                    String childFile = targetDir + "/" + fileName;
                    if (lsEntry.getAttrs().isDir()) {
                        deleteDirRecursive(childFile);
                    } else {
                        channel.rm(childFile);
                    }
                }
            }
            channel.rmdir(targetDir);
        }
    }

    /**
     * 删除文件
     */
    public void deleteFile(String targetFile) throws SftpException {
        if (targetFile == null) {
            throw new IllegalArgumentException("targetFile can't be null");
        }
        if (existsDir(targetFile)) {
            throw new RuntimeException("targetFile is a directory");
        }
        targetFile = winPathToLinuxPath(targetFile);
        channel.rm(targetFile);
    }

    /**
     * 列出目录下的文件和目录
     *
     * @param targetFile 要列出的目录或文件
     */
    public List<String> listFiles(String targetFile) throws SftpException {
        if (targetFile == null) {
            throw new IllegalArgumentException("targetFile can't be null");
        }
        targetFile = winPathToLinuxPath(targetFile);
        Vector<LsEntry> listFiles = channel.ls(targetFile);
        return listFiles.stream().map(LsEntry::getFilename)
                .filter(fileName -> !fileName.equals("."))
                .filter(fileName -> !fileName.equals(".."))
                .collect(Collectors.toList());
    }
    /**
     * 判断文件是否存在(如果targetFile为目录，则认为文件不存在)
     * */
    public boolean existsFile(String targetFileName) throws SftpException {
        if (targetFileName == null) {
            throw new IllegalArgumentException("targetFileName can't be null");
        }
        targetFileName = winPathToLinuxPath(targetFileName);
        try {
            SftpATTRS attr = channel.lstat(targetFileName);
            return !attr.isDir();
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            } else {
                throw e;
            }
        }
    }

    /**
     * 是否存在指定目录
     *
     * @param directory directory
     * @return 是否打开目录
     */
    public boolean existsDir(String directory) throws SftpException {
        if (directory == null) {
            throw new IllegalArgumentException("directory can't be null");
        }
        directory = winPathToLinuxPath(directory);
        try {
            SftpATTRS attr = channel.lstat(directory);
            return attr.isDir();
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            } else {
                throw e;
            }
        }
    }

    /**
     * 在sftp服务器上创建指定目录
     *
     * @param dirName 要创建的目录路径,如:/test1/test2
     */
    public void mkdirs(String dirName) throws SftpException {
        if (dirName == null) {
            throw new RuntimeException("dirName can't be null");
        }
        dirName = winPathToLinuxPath(dirName);
        String cd = "";
        Path path = Paths.get(dirName);
        Iterator<Path> iter = path.iterator();
        while (iter.hasNext()) {
            String name = iter.next().toString();
            String dir = cd + "/" + name;
            if (!existsDir(dir)) {
                channel.mkdir(dir);
            }
            cd = dir;
        }
    }

    public boolean isValid() {
        try {
            channel.pwd();
            return true;
        } catch (SftpException e) {
            logger.info("sftp连接已失效，e.id={},e.message={}",e.id,e.getMessage());
            return false;
        }
    }

    @Override
    public void close() {
        if (channel != null && channel.isConnected()) {
            try {
                channel.disconnect();
            } catch (Exception e) {
                logger.warn("关闭sftp channel异常", e);
                throw e;
            }finally {
                try {
                    channel.getSession().disconnect();
                } catch (JSchException e) {
                    logger.warn("关闭sftp session异常", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**为了在本地测试，将windows路径中的\\转为/
     * window下文件名不能包含\,而linux可以。
     * 一般情况下目录/文件名不会包含\，将\转成/是安全的。
     * 但是万一有哪个xx在文件名里面包含了\呢？
     * 所以正式发布的时候，这里要改一下。
     * @param path
     * @return
     */
    private String winPathToLinuxPath(String path){
        //return path;
        return path.replaceAll("\\\\","/");
    }
}