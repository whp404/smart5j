package com.smart5j.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

@Slf4j
public class ArrayUtil {

    /**
     * 判断数组是否为空
     * @param array
     * @return
     */
    public static boolean isNotEmpty(Object[] array){
        return ArrayUtils.isEmpty(array);
    }


    public static boolean isEmpty(Object[] array){
        return ArrayUtils.isEmpty(array);
    }

}
