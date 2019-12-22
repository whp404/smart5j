package com.smart5j.helper;

import com.smart5j.bean.FileParam;
import com.smart5j.bean.FormParam;
import com.smart5j.bean.Param;
import com.smart5j.util.CollectionUtil;
import com.smart5j.util.FileUtil;
import com.smart5j.util.StreamUtil;
import com.smart5j.util.StringUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: smart5j
 * @author: whp
 * @create: 2019-12-22 15:00
 **/
public class UploadHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadHelper.class);

    private static ServletFileUpload servletFileUpload;

    public static void init(ServletContext servletContext){
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        servletFileUpload = new ServletFileUpload(new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, repository));
        int uploadLimit = ConfigHelper.getAppUploadLimit();
        if(uploadLimit!=0){
            servletFileUpload.setFileSizeMax(uploadLimit*1024*1024);
        }
    }

    /**
     * 判断请求是否为 multipart 类型
     */
    public static boolean isMultipart(HttpServletRequest request) {
        // 判断上传文件的内容是否为 multipart 类型
        return ServletFileUpload.isMultipartContent(request);
    }


    public static Param createParam(HttpServletRequest request) throws IOException{

        ArrayList<FormParam> formParamList = new ArrayList<FormParam>();
        ArrayList<FileParam> fileParamList = new ArrayList<FileParam>();

        try{
            Map<String, List<FileItem>> fileItemListMap = servletFileUpload.parseParameterMap(request);
            if(CollectionUtil.isNotEmpty(fileItemListMap)){
                for(Map.Entry<String,List<FileItem>> fileItemListEntity:fileItemListMap.entrySet()){
                    String fieldName = fileItemListEntity.getKey();
                    List<FileItem> fileItemList = fileItemListEntity.getValue();

                    if(CollectionUtil.isNotEmpty(fileItemList)){

                        for (FileItem fileItem : fileItemList) {
                            if(fileItem.isFormField()){
                                //普通表单项
                                String fieldValue = fileItem.getString("UTF-8");
                                formParamList.add(new FormParam(fieldName,fieldValue));
                            }else{
                                String fileName = FileUtil.getRealFileName(new String(fileItem.getName().getBytes(), "UTF-8"));
                                if(StringUtil.isNotEmpty(fileName)){
                                    long fileSize = fileItem.getSize();
                                    String contentType = fileItem.getContentType();
                                    InputStream inputStream = fileItem.getInputStream();
                                    fileParamList.add(new FileParam(fieldName,fileName,fileSize,contentType,inputStream));
                                }
                            }
                        }

                    }
                }
            }
        } catch (FileUploadException e) {
            LOGGER.error("create param failure",e);
            throw new RuntimeException(e);
        }
        return new Param(formParamList,fileParamList);
    }


    /**
     * 上传文件
     */
    public static void uploadFile(String basePath, FileParam fileParam) {
        try {
            if (fileParam != null) {
                // 创建文件路径（绝对路径）
                String filePath = basePath + fileParam.getFileName();
                FileUtil.createFile(filePath);
                // 执行流复制操作
                InputStream inputStream = new BufferedInputStream(fileParam.getInputStream());
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
                StreamUtil.copyStream(inputStream, outputStream);
            }
        } catch (Exception e) {
            LOGGER.error("上传文件出错！", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 批量上传文件
     */
    public static void uploadFiles(String basePath, List<FileParam> fileParamList) {
        for (FileParam fileParam : fileParamList) {
            uploadFile(basePath, fileParam);
        }
    }
}
