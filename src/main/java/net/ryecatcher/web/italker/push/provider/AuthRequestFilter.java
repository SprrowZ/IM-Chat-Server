package net.ryecatcher.web.italker.push.provider;

import com.google.common.base.Strings;
import net.ryecatcher.web.italker.push.bean.api.base.ResponseModel;
import net.ryecatcher.web.italker.push.bean.db.User;
import net.ryecatcher.web.italker.push.factory.UserFactory;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 * 用于所有的请求的接口的过滤和拦截
 */
@Provider
public class AuthRequestFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        //拿到所有的请求接口
        String relationPath=((ContainerRequest)requestContext).getPath(false);
        //不过滤注册和登陆请求，因为在此之前，用户没有Token值，你过滤了，你就是为难人家
        if (relationPath.startsWith("account/login")||
             relationPath.startsWith("account/register")){
            return;
        }
        //从Headers中去找第一个token节点
        String token=requestContext.getHeaders().getFirst("token");
        if (!Strings.isNullOrEmpty(token)){

            final User self= UserFactory.findByToken(token);
            //如果查询到token信息
            if (self!=null){
                //给当前请求添加一个上下文
                requestContext.setSecurityContext(new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                        //User在这里需要实现Principal接口
                        return self;
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        // TODO: 2020/1/4 ------
                        //可以在此写入用户的权限，role是权限名
                        //可以管理管理员权限
                        return true;
                    }

                    @Override
                    public boolean isSecure() {
                        //HTTPS
                        return false;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        //不用理会
                        return null;
                    }
                });
            }
            //写入上下文后返回
            return ;
        }
        //直接返回一个账户需要登录的model
        ResponseModel model=ResponseModel.buildAccountError();

        //停止一个请求的继续下发，调用该方法后直接返回请求
        //不会走到Service中去
        Response response=Response.status(Response.Status.OK)
                .entity(model)
                .build();
        requestContext.abortWith(response);

    }
}
