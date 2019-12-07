package com.smart5j;

import com.smart5j.helper.AopHelper;
import com.smart5j.helper.ClassHelper;
import com.smart5j.util.BeanHelper;
import com.smart5j.util.ClassUtil;
import com.smart5j.util.ControllerHelper;

/**
 * 加载相应的 Helper 类
 *
 * @author huangyong
 * @since 1.0.0
 */
public final class HelperLoader {

    public static void init() {
        Class<?>[] classList = {
            ClassHelper.class,
            BeanHelper.class,
           AopHelper.class,
           // IocHelper.class,
            ControllerHelper.class
        };
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName());
        }
    }
}