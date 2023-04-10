package com.ldp.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldp.seckill.VO.LoginVo;
import com.ldp.seckill.VO.RespBean;
import com.ldp.seckill.pojo.Goods;
import com.ldp.seckill.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUserService  extends IService<User> {
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);
    RespBean updatePassword(String userTicket,String password,HttpServletRequest request,HttpServletResponse response);

}
