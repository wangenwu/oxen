package com.baren.bison.pool;

import java.util.List;

/**
 * 路由功能, 均衡的从一组对象中选择一个
 */
public interface BalanceRoute<T> {

    /**
     * 均衡选取一个
     * @return
     */
    T select(List<T> list);

}
