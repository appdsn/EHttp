package com.appdsn.ehttpdemo;

import java.io.Serializable;

public class UserInfo implements Serializable {
    public int userID ;
    public String mobile ;
    public String token ;
    public String name ;
    public String avatar ;// 头像url
    public String jid ;
    public int deviceType ;
    public String createTime ;
    public boolean isLogin;
    public String popActivityTitle;
}
