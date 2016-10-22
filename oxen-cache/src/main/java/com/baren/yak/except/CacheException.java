package com.baren.yak.except;

/**
 * Created by user on 16/7/30.
 */
public class CacheException extends RuntimeException {

//    public static final int CACHE_ERR_TYPE_TIMEOUT = 1;
//    public static final int CACHE_ERR_TYPE_TIMEOUT = 1 << 1;

    public CacheException(final Exception cause) {
        super(cause);
    }
}
