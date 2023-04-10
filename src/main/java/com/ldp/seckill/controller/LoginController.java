package com.ldp.seckill.controller;


import com.ldp.seckill.VO.LoginVo;
import com.ldp.seckill.VO.RespBean;
import com.ldp.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/login")
public class LoginController {

    /**
     * 查询列表
     * 优化前 QPS:201
     */
    @Autowired
    private IUserService userService;

    //http://localhost:8080/login/toLogin
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        RespBean respBean = userService.doLogin(loginVo, request, response);
        //System.out.println(respBean.getCode()+" "+respBean.getMessage());
        return respBean;
    }

}
