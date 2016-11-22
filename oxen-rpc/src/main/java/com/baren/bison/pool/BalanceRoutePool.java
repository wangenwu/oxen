package com.baren.bison.pool;

/**
 * Created by user on 16/11/15.
 */
public abstract class BalanceRoutePool<T> implements Pool<T> {

    private BalanceRoute<T> route;


    public BalanceRoutePool(BalanceRoute route) {
        this.route = route;
    }

//    private
//
//    public T get() {
//
//    }



}
