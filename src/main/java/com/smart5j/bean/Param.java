package com.smart5j.bean;


import java.util.Map;

import com.smart5j.util.CastUtil;


/**
 * 请求参数对象
 *
 * @author huangyong
 * @since 1.0.0
 */
public class Param {

    private Map<String,Object> paramMap;

    public Param(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public long getLong(String name){
        return CastUtil.castLong(paramMap.get(name));
    }

    public Map<String, Object> getMap() {
        return paramMap;
    }
}
