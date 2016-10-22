package com.cuan.helper.reflect;

/**
 * Created by genglei-cuan on 16-10-22.
 */
/**
 * 来自 360 DP 框架中的带缓存的反射组件
 */
class Validate {
    static void isTrue(final boolean expression, final String message, final Object... values) {
        if (expression == false) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }
}
