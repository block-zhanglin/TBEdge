package com.zl.blockchain.util;


/**
 *计算工具类
 */
public class MathUtil {

    /**
     * min 函数：求出a，b中的更小的那个
     * @param a
     * @param b
     * @return
     */
    public static int min(int a, int b) {
        return (a <= b) ? a : b;
    }

    /**
     * 随机产生一个float【1----1000】
     * @return
     */
    public static float randx(){
        return (float)Math.random()*1000+1;
    }
}
