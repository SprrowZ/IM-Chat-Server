package net.ryecatcher.web.italker.push.service;

import net.ryecatcher.web.italker.push.bean.db.User;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * 6-11
 */
public class BaseService {
    //请求过滤之加上注解，自动识别--具体返回值为我们的拦截器中所返回的SecurityContext
    @Context
    protected SecurityContext securityContext;

    /**
     * 从上下文中直接获取信息
     * @return
     */
    protected User getSelf(){
        return  (User)securityContext.getUserPrincipal();
    }

}
