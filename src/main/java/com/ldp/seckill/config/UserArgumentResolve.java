package com.ldp.seckill.config;

import com.ldp.seckill.pojo.User;
import com.ldp.seckill.service.impl.UserServiceImpl;
import com.ldp.seckill.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserArgumentResolve implements HandlerMethodArgumentResolver {
    @Autowired
    private UserServiceImpl userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //会自动获取下面那个方法的返回值，改返回值的对象的类信息，是否是User类型，如果是，则证明已经登录
        Class<?> clazz = parameter.getParameterType();
        return clazz == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory binderFactory) throws Exception {
        return UserContext.getUser();
    }
}
