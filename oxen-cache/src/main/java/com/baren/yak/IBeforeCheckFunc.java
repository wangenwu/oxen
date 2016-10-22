package com.baren.yak;

import java.lang.reflect.Method;

/**
 * Created by user on 16/8/2.
 */
public interface IBeforeCheckFunc {


    Object beforeCheck(Object target, Method method, Object... params);
}
