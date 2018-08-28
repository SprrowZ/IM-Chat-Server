package net.ryecatcher.web.italker.push.service;

import net.ryecatcher.web.italker.push.bean.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 描述:
 * 注册包测试类
 *
 * @Author Zzg
 * @Create 2018-08-25 22:58
 */
@Path("/account")//注册路径访问，所有映射想走到本类，访问路径就为：
// 和web.xml最底下的映射路径结合即为：127.0.0.1/api/account...
public class AccountService {
    //实际路径 127.0.0.1/api/account/login
    @GET
    @Path("/login")
    public String get(){
        return  "You get the login";
    }
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)//传入json
    @Produces(MediaType.APPLICATION_JSON)//输出json
    public User post(){
        User user=new User();
        user.setName("RyeCatcher");
        user.setSex("男");
        return user;
    }
}
























































