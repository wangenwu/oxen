package com.baren.bison.pool;

/**
 * 可重用对象资源池,主要对一些创建耗时对象进行池化,提高性能,比如数据库连接,rpc连接等等.
 */
public interface Pool<T> {

    /**
     * 从池中获取一个对象,如果没有,可以阻塞,也可以异步返回,由实现来决定.
     * @return
     */
    T get();

    /**
     * 释放资源回到池中,以便继续复用,这里需要是异步操作,方便快速返回.
     * @param t
     */
    void release(T t);

    /**
     * 关闭池,需要关闭池中每个资源
     */
    void shutdown();

    /**
     * 验证对象接口,主要服务于Pool
     * @param <T>
     */
    interface Validator <T> {

        /**
         * 判断对象是否可用
         * @param t
         * @return
         */
        boolean isValid(T t);

        /**
         * 销毁对象
         * @param t
         */
        void destroy(T t);
    }
}
