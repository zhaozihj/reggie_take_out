package com.itheima.reggie.config;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import javafx.beans.binding.LongExpression;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.UserException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI=request.getRequestURI();
        log.info("请求已经拦截{}",requestURI);
        //获取session
        HttpSession session = request.getSession();
        //获取session中employee
        Long employee = (Long) session.getAttribute("employee");

        Long user=(Long) session.getAttribute("user");
        if(employee!=null){
            //把id放到ThreadLocal
            BaseContext.setCurrentId(employee);
            //放行
            return true;
        }

        if(user!=null){
            BaseContext.setCurrentId(user);
            return true;
        }

        //如果未登录返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return false;

    }
}
