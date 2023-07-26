package com.zl.blockchain.util;

/**
 *字符串处理类
 */
public class StringUtil {

    public static final String BLANKSPACE = " ";  //空白间隔

    /**
     * 判断字符串 string 和 anotherstring字符串是否相等
     * @param string
     * @param anotherString
     * @return
     */
    public static boolean equals(String string, String anotherString){
        return string.equals(anotherString);
    }

    /**
     * 判断字符串string 是否为空
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {  //字符串是否为空

        return string == null || string.isEmpty();
    }


    /**
     *向原始数据（string）添加前缀（paddingvalue）
     * @param rawValue  原始数据
     * @param targetLength  目标长度
     * @param paddingValue  添加值
     * @return
     */
    public static String prefixPadding(String rawValue,int targetLength,String paddingValue) {  //添加字符串前缀
        String target = rawValue;
        while (target.length() < targetLength){
            target = paddingValue + target;
        }
        return target;
    }


    /**
     * 将字符串1 和 字符串2 连接起来
     * @param value1 字符串1
     * @param value2  字符串2
     * @return
     */
    public static String concatenate(String value1,String value2) {  //2个字符串连接
        return value1 + value2;
    }

    /**
     * 将字符串1、字符串2、字符串3连接起来
     * @param value1 字符串1
     * @param value2 字符串2
     * @param value3 字符串3
     * @return
     */
    public static String concatenate3(String value1, String value2, String value3) {  //3个字符串连接

        return value1 + value2 + value3;
    }

    /**
     * 将long型整数转为string类型
     * @param number long型整数
     * @return
     */
    public static String valueOfUint64(long number) {  //long型整数转为string类型

        return String.valueOf(number);
    }

    /**
     * 获取字符串的长度
     * @param value 字符串
     * @return
     */
    public static long length(String value) {  //获取字符串长度

        return value.length();
    }
}
