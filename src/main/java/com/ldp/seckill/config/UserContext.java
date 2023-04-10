package com.ldp.seckill.config;

import com.ldp.seckill.pojo.User;

public class UserContext {
    private static ThreadLocal<User> userHolder=new ThreadLocal<>();

    public static void setUser(User user){
        userHolder.set(user);
    }

    public static  User getUser(){
       return  userHolder.get();
    }
}
