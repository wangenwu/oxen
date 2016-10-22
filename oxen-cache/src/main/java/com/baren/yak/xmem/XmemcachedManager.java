package com.baren.yak.xmem;

import com.baren.yak.ICacheClient;
import com.baren.yak.ICacheManager;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 * 在xml中进行配置,主要是配置server方便
 * Created by user on 16/7/27.
 */


public class XmemcachedManager implements InitializingBean, ICacheManager {

    private static final Logger log = LoggerFactory.getLogger(XmemcachedManager.class);

    private Map<String, XmemCacheClientImpl> memBuildMap = new ConcurrentHashMap<String, XmemCacheClientImpl>(16);

    private Map<String, String> servers;

    public Map<String, String> getServers() {
        return servers;
    }

    public void setServers(Map<String, String> servers) {
        this.servers = servers;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (servers==null) {
            log.error("no memcached servers set!");
            return;
        }
        Iterator<Map.Entry<String, String>> it = servers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();


            XMemcachedClientBuilder build = new XMemcachedClientBuilder(entry.getValue());
            build.setOpTimeout(2000);  // 2秒操作超时
            build.setConnectionPoolSize(10);  // 连接池大小
            build.setConnectTimeout(6000); // 6秒建立连接超时
            build.setTranscoder(new SerializingTranscoder());
            build.setSessionLocator(new KetamaMemcachedSessionLocator());
            build.setCommandFactory(new BinaryCommandFactory());
            XMemcachedClient client = (XMemcachedClient) build.build();
            memBuildMap.put(entry.getKey(), new XmemCacheClientImpl(entry.getKey(), client));
        }
    }

    public ICacheClient getCache(String name) {

        return this.memBuildMap.get(name);
    }

    public Collection<String> getCacheNames() {
        return this.memBuildMap.keySet();
    }

}
