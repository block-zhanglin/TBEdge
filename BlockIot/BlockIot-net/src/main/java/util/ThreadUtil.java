package util;

/**
 *线程工具类
 */
public class ThreadUtil {

    /**
     * 线程睡眠时间
     * @param millisecond
     */
    public static void millisecondSleep(long millisecond){//参数 millisencond 千分之一秒
        try {
            Thread.sleep(millisecond); //线程睡眠千分之一秒
        } catch (InterruptedException e) {  //阻塞异常,线程睡眠失败
            throw new RuntimeException("sleep failed.",e);
        }
    }
}
