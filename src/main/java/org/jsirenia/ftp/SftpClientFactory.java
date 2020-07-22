package org.jsirenia.ftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sfpay.msfs.jyd.common.info.FtpConnParam;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * SftpClient工厂
 */
public class SftpClientFactory extends BasePooledObjectFactory<SftpClient> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Properties sshConfig = new Properties();

    /**
     * Properties#getProperty方法，如果value不是String类型，会返回null。
     * 所以需要调用者保证value必须为String类型
     * */
    public SftpClientFactory(Properties sshConfig){
        this.sshConfig.put("StrictHostKeyChecking", "no");
        this.sshConfig.putAll(sshConfig);
    }
    public SftpClientFactory(FtpConnParam param){
        this.sshConfig.put("StrictHostKeyChecking", "no");
        this.sshConfig.put("host",String.valueOf(param.getHost()));
        this.sshConfig.put("port",String.valueOf(param.getPort()));
        this.sshConfig.put("username",String.valueOf(param.getUserName()));
        this.sshConfig.put("password",String.valueOf(param.getPassword()));
    }
    /**
     * 创建一个{@link SftpClient}实例
     * 这个方法必须支持并发多线程调用
     *
     * @return {@link SftpClient}实例
     */
    @Override
    public SftpClient create() throws JSchException {
        String host = sshConfig.getProperty("host");
        String port = sshConfig.getProperty("port");
        String username = sshConfig.getProperty("username");
        String password = sshConfig.getProperty("password");
        Session sshSession = null;
        ChannelSftp channel = null;
        try {
            JSch jsch = new JSch();
            sshSession = jsch.getSession(username, host, Integer.parseInt(port));
            sshSession.setPassword(password);
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            channel = (ChannelSftp) sshSession.openChannel("sftp");
            channel.connect();
            return new SftpClient(channel);
        } catch (JSchException e) {
            closeChannel(channel);
            closeSession(sshSession);
            throw e;
        }
    }

    private void closeSession(Session sshSession){
        if(sshSession==null || !sshSession.isConnected()){
            return;
        }
        try{
            sshSession.disconnect();
        }catch (Exception e){
            logger.error("关闭session异常",e);
        }
    }
    private void closeChannel(ChannelSftp channel){
        if(channel==null || !channel.isConnected()){
            return;
        }
        try{
            channel.disconnect();
        }catch (Exception e){
            logger.error("关闭channel异常",e);
        }
    }
    /**
     * 用{@link PooledObject}的实例包装对象
     * @param sftp 被包装的对象
     * @return 对象包装器
     */
    @Override
    public PooledObject<SftpClient> wrap(SftpClient sftp) {
        return new DefaultPooledObject<>(sftp);
    }

    /**
     * 销毁对象
     *
     * @param pooledObject 对象包装器
     */
    @Override
    public void destroyObject(PooledObject<SftpClient> pooledObject) {
        if (pooledObject!=null) {
            SftpClient sftp = pooledObject.getObject();
            if (sftp!=null) {
                sftp.close();
            }
        }
    }

    /**
     * 检查连接是否可用
     * @param pooledObject 对象包装器
     * @return {@code true} 可用，{@code false} 不可用
     */
    @Override
    public boolean validateObject(PooledObject<SftpClient> pooledObject) {
        if (pooledObject!=null) {
            SftpClient sftp = pooledObject.getObject();
            if (sftp!=null) {
                return sftp.isValid();
            }
        }
        return false;
    }
}
