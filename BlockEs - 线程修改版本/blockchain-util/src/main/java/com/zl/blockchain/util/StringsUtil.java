package com.zl.blockchain.util;

import java.util.*;

/**
 *字符串列表处理类
 */
public class StringsUtil {

    /**
     * 判断string 列表中是否有重复元素
     * @param values
     * @return
     */
    public static boolean hasDuplicateElement(List<String> values) { //List列表元素的个数
        Set<String> set = new HashSet<>(values);
        return values.size() != set.size();
    }

    /**
     * 判断string数组values里面是否包含value
     * @param values
     * @param value
     * @return
     */
    public static boolean contains(List<String> values, String value) {  //List列表元素中是否包含value
        if(values == null){
            return false;
        }
        if(value == null){
            return false;
        }
        return values.contains(value);
    }

    /**
     * values用valueseparator为标准划分
     * @param values
     * @param valueSeparator
     * @return
     */
    public static List<String> split(String values, String valueSeparator) {   //values以valueSeparator为标准划分
        if(StringUtil.isEmpty(values)){
            return new ArrayList<>();
        }
        return Arrays.asList(values.split(valueSeparator));  //Arrays.asList()将数组转换为List
    }
}
