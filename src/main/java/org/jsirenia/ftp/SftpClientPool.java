package org.jsirenia.ftp;

import com.sfpay.msfs.jyd.common.util.Callbacks;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * sftp连接池
 */
public class SftpClientPool extends GenericObjectPool<SftpClient> {

    /**
     * Creates a new <code>GenericObjectPool</code> that tracks and destroys
     * objects that are checked out, but never returned to the pool.
     * 创建一个{@link GenericObjectPool}对象池，跟踪使用后未返回给对象池的对象，防止对象泄漏。
     *
     * @param factory         The object factory to be used to create object instances
     *                        used by this pool
     *                        对象工厂
     * @param config          The base pool configuration to use for this pool instance.
     *                        The configuration is used by value. Subsequent changes to
     *                        the configuration object will not be reflected in the
     *                        pool.
     *                        对象池配置
     * @param abandonedConfig Configuration for abandoned object identification
     *                        废弃对象跟踪配置
     */
    public SftpClientPool(SftpClientFactory factory, GenericObjectPoolConfig<SftpClient> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
    public void doWithSftpClient(Callbacks.Callback10<SftpClient> cb){
        SftpClient client = null;
        try{
            client = borrowObject(5000);
            cb.apply(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(client!=null){
                returnObject(client);
            }
        }
    }
    public <T> T doWithSftpClient(Callbacks.Callback11<T,SftpClient> cb){
        SftpClient client = null;
        try{
            client = borrowObject(5000);
            return cb.apply(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(client!=null){
                returnObject(client);
            }
        }
    }

}
