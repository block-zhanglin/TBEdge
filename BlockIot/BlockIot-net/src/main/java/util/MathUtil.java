package util;


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

    public static float randx(){
        return (float)Math.random()*1000+1;
    }
}
