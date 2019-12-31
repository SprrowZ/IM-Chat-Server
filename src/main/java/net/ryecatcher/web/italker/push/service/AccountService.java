package net.ryecatcher.web.italker.push.service;



import net.ryecatcher.web.italker.push.bean.api.account.RegisterModel;
import net.ryecatcher.web.italker.push.bean.card.UserCard;
import net.ryecatcher.web.italker.push.bean.db.TestBean;
import net.ryecatcher.web.italker.push.bean.db.User;
import net.ryecatcher.web.italker.push.factory.UserFactory;

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
// 和web.xml最底下的映射路径结合即为：http://localhost:8080/api/account/...
public class AccountService {
    //实际路径 http://localhost:8080/api/account/login
    @GET
    @Path("/login")
    public String get(){
        return  "You get the login";
    }


    /**
     * 登录
     * @return
     */
    @POST
    @Path("/login")
    @Consumes()//传入json
    @Produces(MediaType.APPLICATION_JSON)//输出json
    public TestBean post(){
//        User user=new User();
//        user.setName("RyeCatcher");
//        user.setSex(1);
 TestBean bean=new TestBean();
 bean.setPassword("123");
 bean.setUsrname("Rye");
        return bean;
    }

    /**
     * 注册
     * @param model
     * @return
     */
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)//传入json
    @Produces(MediaType.APPLICATION_JSON)//输出json
    public UserCard register(RegisterModel model){//注册需要传入一些信息，然后返回User,但是
        //手机号是否已经被注册
        User user=UserFactory.findByPhone(model.getAccount().trim());
        if (user!=null){
             UserCard card=new UserCard();
             card.setName("该手机号已经被注册过了！");
             return card;
        }
         //名字是否已经被注册
         user=UserFactory.findByName(model.getName().trim());
        if (user!=null){
            UserCard card=new UserCard();
            card.setName("该用户名已经被注册过了！");
            return card;
        }


         user= UserFactory.register(model.getAccount()
                ,model.getPassword()
                ,model.getName());

        if (user!=null){
            UserCard card=new UserCard();
            card.setName(model.getName());
            card.setPhone(user.getPhone());
            card.setSex(user.getSex());
            card.setIsFollow(true);
            card.setModifyAt(user.getUpdateAt());
            return card;
        }
        return null;
    }
}
























































