package com.smart5j.helper;

import com.smart5j.bean.FormParam;
import com.smart5j.bean.Param;
import com.smart5j.util.ArrayUtil;
import com.smart5j.util.CodecUtil;
import com.smart5j.util.StreamUtil;
import com.smart5j.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @program: smart5j
 * @author: whp
 * @create: 2019-12-22 16:08
 * 主要是将request 解析，解析表单参数
 **/
public class RequestHelper {


    public static Param createParam(HttpServletRequest request) throws IOException {
        ArrayList<FormParam> formParamList = new ArrayList<FormParam>();
        formParamList.addAll(parseParameterNames(request));
        formParamList.addAll(parseInputStream(request));
        return  new Param(formParamList);
    }

    /**
     * 解析报文体的参数
     * @param request
     * @return
     */
    private static List<FormParam> parseParameterNames(HttpServletRequest request){
        ArrayList<FormParam> formParamList = new ArrayList<FormParam>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()){
            String fieldName = parameterNames.nextElement();
            String[] fieldValues = request.getParameterValues(fieldName);
            if(ArrayUtil.isNotEmpty(fieldValues)){
                Object fieldValue;
                if(fieldValues.length==1){
                    fieldValue = fieldValues[0];
                }else{
                    StringBuilder sb = new StringBuilder("");
                    for(int i=0;i<fieldValues.length;i++){
                        sb.append(fieldValues[i]);
                        if(i!=fieldValues.length-1){
                            sb.append(StringUtil.SEPARATOR);
                        }
                    }
                    fieldValue = sb.toString();
                }
                formParamList.add(new FormParam(fieldName,fieldValue));
            }
        }
        return formParamList;
    }



    private static List<FormParam> parseInputStream(HttpServletRequest request) throws IOException {
        ArrayList<FormParam> formParamList = new ArrayList<FormParam>();
        String body = CodecUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        if(StringUtil.isNotEmpty(body)){
            String[] kvs = StringUtils.split(body, "&");
            if(ArrayUtil.isNotEmpty(kvs)){
                for (String kv : kvs) {
                    String[] array = StringUtils.split(kv, "=");
                    if(ArrayUtil.isNotEmpty(array) && array.length==2){
                        String fieldName = array[0];
                        String fieldValue = array[1];
                        formParamList.add(new FormParam(fieldName,fieldValue));
                    }
                }
            }
        }
        return formParamList;
    }
}
