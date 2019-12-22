package com.smart5j;

import com.smart5j.bean.Data;
import com.smart5j.bean.Handler;
import com.smart5j.bean.Param;
import com.smart5j.bean.View;
import com.smart5j.helper.ConfigHelper;
import com.smart5j.helper.RequestHelper;
import com.smart5j.helper.ServletHelper;
import com.smart5j.helper.UploadHelper;
import com.smart5j.util.*;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.*;

@WebServlet(urlPatterns = "/*",loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {


    @Override
    public void init(ServletConfig servletConfig) throws ServletException{
        HelperLoader.init();
        ServletContext servletContext = servletConfig.getServletContext();
        //注册Jsp 的 servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath()+"*");
        // 注册处理静态资源的默认servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath()+"*");

        //文件上传的初始化设置
        UploadHelper.init(servletContext);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ServletHelper.init(request,response);
        try {
            String requestMethod = request.getMethod().toLowerCase();
            String requestPath = request.getPathInfo();

            if (requestPath.equals("/favicon.ico")) {
                return;
            }
            Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);//获取Action处理器
            if (handler != null) {

                Class<?> controllerClass = handler.getControllerClass();
                Object controllerBean = BeanHelper.getBean(controllerClass);

                Param param = null;
                // 请求中包含请求参数
                if (UploadHelper.isMultipart(request)) {
                    param = UploadHelper.createParam(request);
                } else {
                    param = RequestHelper.createParam(request);
                }

                Method actionMethod = handler.getActionMethod();
                Object result = null;
                if (param.isEmpty()) {
                    result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
                } else {
                    //获取方法返回值
                    result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
                }

                if (result instanceof View) {

                    View view = (View) result;
                    String path = view.getPath();
                    if (StringUtil.isNotEmpty(path)) {
                        if (path.startsWith("/")) {
                            response.sendRedirect(request.getContextPath() + path);
                        } else {
                            Map<String, Object> model = view.getModel();

                            for (Map.Entry<String, Object> entry : model.entrySet()) {
                                request.setAttribute(entry.getKey(), entry.getValue());
                            }
                            request.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(request, response);
                        }
                    }
                } else if (result instanceof Data) {

                    Data data = (Data) result;
                    Object model = data.getModel();
                    if (model != null) {
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        PrintWriter writer = response.getWriter();
                        String json = JsonUtil.toJson(model);
                        writer.write(json);
                        writer.flush();
                        writer.close();
                    }
                }
            }
        }finally {
            ServletHelper.destroy();
        }
    }



}
