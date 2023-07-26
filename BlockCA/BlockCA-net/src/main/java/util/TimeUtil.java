package util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *时间工具类
 */
public class TimeUtil {


    /**
     * 千分之一秒时间戳 ==当前时刻到1970-1-01 00:00:00.000 的时间距离，类型是long（比new data()的效率更高）
     * 3ed@return
     */
    public static long millisecondTimestamp(){
        return System.currentTimeMillis();
    }

    /**
     * java.text.SimpleDateFormat来自定义格式。
     * @param millisecondTimestamp
     * @return
     */
    public static String formatMillisecondTimestamp(long millisecondTimestamp) {
        Date date = new Date(millisecondTimestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return simpleDateFormat.format(date);
    }
}
