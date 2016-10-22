package com.baren.yak.xmem;

import com.baren.yak.ICacheClient;
import com.baren.yak.except.CacheException;
import com.google.gson.Gson;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.transcoders.StringTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * Created by user on 16/7/30.
 */

public class XmemCacheClientImpl implements ICacheClient {

    private static final Logger log = LoggerFactory.getLogger(XmemCacheClientImpl.class);

    private String name;
    private XMemcachedClient client;

    public XmemCacheClientImpl(String name, XMemcachedClient client) {
        this.name = name;
        this.client = client;
    }

    @Override
    public <T> boolean add(String key, int exp, T value) {
        Gson gson = new Gson();
        String json = gson.toJson(value);
        try {
            return this.client.add(key, exp, json, new StringTranscoder());
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Gson gson = new Gson();
        try {
            String obj = this.client.get(key, new StringTranscoder());
            return gson.fromJson(obj, clazz);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Map<String, Object> multiGet(Collection<String> keys) {
        return null;
    }

    @Override
    public void set(String key, int exp, Object value) {

    }

    @Override
    public boolean delete(String key) {
        return false;
    }

    @Override
    public void delete(Collection<String> keys) {

    }

    @Override
    public long decr(String key, int by) {
        return 0;
    }

    @Override
    public long incr(String key, int by, long def) {
        return 0;
    }

    @Override
    public long incr(String key, int by, long def, int exp) {
        return 0;
    }

    @Override
    public Long getCounter(String cacheKey) {
        return null;
    }

    @Override
    public void setCounter(String cacheKey, int expiration, long value) {

    }

    @Override
    public Object getNativeClient() {
        return null;
    }
//
//    @Override
//    public Collection<SocketAddress> getAvailableServers() {
//        ArrayList servers = new ArrayList();
//        Collection inetSocketAddresses = this.client.getAvailableServers();
//        if(inetSocketAddresses != null && !inetSocketAddresses.isEmpty()) {
//            servers.addAll(inetSocketAddresses);
//        }
//
//        return servers;
//    }

//    @Override
//    public String getName() {
//        return this.name;
//    }

//    @Override
//    public <T> boolean add(String key, int exp, T value, SerializationType serializationType) throws TimeoutException, CacheException {
//        switch (serializationType){
//            case JSON:
//                Gson gson = new Gson();
//                String json = gson.toJson(value);
//                return this.baseAddThrow(key, exp, json, new StringTranscoder());
//            case BINARY:
//                return this.baseAddThrow(key, exp, value, new SerializingTranscoder());
//        }
//        return false;
//    }
//
//    private <T> boolean baseAddThrow(String key, int exp, T value, Transcoder<T> transcoder) throws CacheException, TimeoutException {
//        try {
//            return this.client.add(key, exp, value, transcoder);
//        } catch (InterruptedException e) {
//            throw new CacheException(e);
//        } catch (MemcachedException e) {
//            throw new CacheException(e);
//        }
//    }
//
//    private <T> boolean baseAddCatch(String key, int exp, T value, Transcoder<T> transcoder) {
//        try {
//            return this.client.add(key, exp, value, transcoder);
//        } catch (InterruptedException e) {
//            log.error(e.toString());
//        } catch (MemcachedException e) {
//            log.error(e.toString());
//        } catch (TimeoutException e) {
//            log.error(e.toString());
//        }
//        return false;
//    }
//
//    @Override
//    public <T> boolean addSilently(String key, int exp, T value, SerializationType serializationType) {
//        switch (serializationType){
//            case JSON:
//                Gson gson = new Gson();
//                String json = gson.toJson(value);
//                return this.baseAddCatch(key, exp, json, new StringTranscoder());
//            case BINARY:
//                return this.baseAddCatch(key, exp, value, new SerializingTranscoder());
//        }
//
//        return false;
//    }
//
//    @Override
//    public long decr(String key, int by) throws TimeoutException, CacheException {
//        try {
//            this.client.decr(key, by);
//        } catch (InterruptedException e) {
//            throw new CacheException(e);
//        } catch (MemcachedException e) {
//            throw new CacheException(e);
//        }
//        return 0;
//    }
//
//    @Override
//    public boolean delete(String key) throws TimeoutException, CacheException {
//        try {
//            this.client.delete(key);
//        } catch (InterruptedException e) {
//            throw new CacheException(e);
//        } catch (MemcachedException e) {
//            throw new CacheException(e);
//        }
//        return false;
//    }
//
//    @Override
//    public void delete(Collection<String> keys) throws TimeoutException, CacheException {
//        if (keys == null || keys.isEmpty()) {
//            return;
//        }
//
//        for (final String key : keys) {
//            if (key != null) {
//                delete(key);
//            }
//        }
//    }
//
//    private <T> T baseGetThrow(String key, Transcoder<T> transcoder) throws CacheException, TimeoutException {
//        try {
//            return this.client.get(key, transcoder);
//        } catch (InterruptedException e) {
//            throw new CacheException(e);
//        } catch (MemcachedException e) {
//            throw new CacheException(e);
//        }
//    }
//
//    @Override
//    public <T> T get(String key, SerializationType serializationType, Class<T> clazz) throws TimeoutException, CacheException {
//        switch (serializationType) {
//            case JSON:
//                Gson gson = new Gson();
//                String v = this.baseGetThrow(key, new StringTranscoder());
//                return gson.fromJson(v, clazz);
//            case BINARY:
//                this.baseGetThrow(key, new SerializingTranscoder());
//        }
//        return null;
//    }
//
//    @Override
//    public Map<String, Object> multiGet(Collection<String> keys, SerializationType serializationType) throws TimeoutException, CacheException {
//        return null;
//    }
//
//    @Override
//    public long incr(String key, int by, long def) throws TimeoutException, CacheException {
//        return 0;
//    }
//
//    @Override
//    public long incr(String key, int by, long def, int exp) throws TimeoutException, CacheException {
//        return 0;
//    }
//
//    @Override
//    public <T> void set(String key, int exp, Object value, SerializationType serializationType) throws TimeoutException, CacheException {
//
//    }
//
//    @Override
//    public Long getCounter(String cacheKey) throws TimeoutException, CacheException {
//        return null;
//    }
//
//    @Override
//    public void setCounter(String cacheKey, int expiration, long value) throws TimeoutException, CacheException {
//
//    }
//
//    @Override
//    public Object getNativeClient() {
//        return this.client;
//    }
}
