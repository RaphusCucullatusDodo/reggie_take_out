package com.raphus.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.raphus.reggie.common.BaseContext;
import com.raphus.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter{
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次拦截请求的URI(HttpServletResponse域对象获取请求头信息)
        String requestURI = request.getRequestURI();// /backend/index.html
        log.info("拦截到请求：{}",requestURI);

        //定义不需要处理的请求路径(即不登入即可访问的路径urls)
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/user/sendMsg",
                "/user/login",
                "/common/**",
                "/backend/**",
                "/front/**"
        };

        //2、判断本次请求是否需要处理(判断本次请求路径是否在urls数组中)
        boolean check = check(urls, requestURI);

        //3、如果不需要处理，则直接放行(check=ture则放行)
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        log.info("当前线程为:{}",Thread.currentThread().getName());
        //todo 当前拦截规则只要user登入则可直接访问后台管理页面不合理(应隔离user与employee账户权限)
        //4-1、判断登录状态，如果已登录，则直接放行(check=false,则判断是否登入)
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            //登入时将数据库(employee表)查到的员工信息存入Session中,此时用作登入验证,即employee不为空则视为已登入
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //4-2、判断 移动端(user表) 登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
