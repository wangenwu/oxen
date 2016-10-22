package com.baren.yak;


import java.util.Collection;
import java.util.Map;

/**
 * Created by user on 16/7/30.
 */
public interface ICacheClient {


    <T> boolean add(String key, int exp, T value);
    <T> T get(String key, Class<T> clazz);
//    Object get(String key);
    Map<String, Object> multiGet(Collection<String> keys);
    void set(String key, int exp, Object value);

    boolean delete(String key);
    void delete(Collection<String> keys);


    long decr(String key, int by);
    long incr(String key, int by, long def);
    long incr(String key, int by, long def, int exp);


    Long getCounter(String cacheKey);

    void setCounter(String cacheKey, int expiration, long value);

    Object getNativeClient();
    
}
