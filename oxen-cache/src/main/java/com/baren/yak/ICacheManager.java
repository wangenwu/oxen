package com.baren.yak;

import java.util.Collection;

/**
 * Created by user on 16/8/31.
 */
public interface ICacheManager {

    ICacheClient getCache(String name);

    Collection<String> getCacheNames();
}
