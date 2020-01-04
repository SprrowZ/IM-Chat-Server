package net.ryecatcher.web.italker.push.service;


import com.google.common.base.Strings;
import net.ryecatcher.web.italker.push.bean.api.account.AccountRspModel;
import net.ryecatcher.web.italker.push.bean.api.account.LoginModel;
import net.ryecatcher.web.italker.push.bean.api.account.RegisterModel;
import net.ryecatcher.web.italker.push.bean.api.base.ResponseModel;
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
public class AccountService extends BaseService {//登录注册，不能用getSelf，没token

    //实际路径 http://localhost:8080/api/account/login
    @GET
    @Path("/login")
    public String get() {
        return "You get the login";
    }

    /**
     * 登录
     *
     * @return
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> post(LoginModel model) {
        if (!LoginModel.check(model)) {//对参数进行校验
            return ResponseModel.buildParameterError();
        }


        User user = UserFactory.login(model.getAccount(), model.getPassword());
        if (user != null) {
            if (!Strings.isNullOrEmpty(model.getPushId())) {
                return bind(user, model.getPushId());//绑定设备ID
            }
            //绑定成功返回用户信息
            AccountRspModel rspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(rspModel);

        } else {
            return ResponseModel.buildLoginError();
        }
    }

    /**
     * 注册
     *
     * @param model
     * @return
     */
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)//传入json
    @Produces(MediaType.APPLICATION_JSON)//输出json
    public ResponseModel<AccountRspModel> register(RegisterModel model) {//注册需要传入一些信息，然后返回User,但是
        //手机号是否已经被注册

        if (!RegisterModel.check(model)) {//对输入参数进行校验
            return ResponseModel.buildParameterError();
        }

        User user = UserFactory.findByPhone(model.getAccount().trim());
        if (user != null) {
            return ResponseModel.buildHaveAccountError();
        }
        //名字是否已经被注册
        user = UserFactory.findByName(model.getName().trim());
        if (user != null) {
            return ResponseModel.buildHaveNameError();
        }

        //开始注册逻辑
        user = UserFactory.register(model.getAccount()
                , model.getPassword()
                , model.getName());

        if (user != null) {//已经注册成功
            if (!Strings.isNullOrEmpty(model.getPushId())) {
                return bind(user, model.getPushId());//绑定设备ID
            }
            AccountRspModel accountModel = new AccountRspModel(user);
            return ResponseModel.buildOk(accountModel);
        } else {//异常错误
            return ResponseModel.buildRegisterError();
        }

    }


    /**
     * 绑定设备ID
     *--------------------------这里token可以去掉了，拦截里已经加了，这里就是留个纪念
     * @return
     */
    @POST
    @Path("/bind/{pushId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRspModel> bind(@HeaderParam("token") String token,
                                               @PathParam("pushId") String pushId) {
        if (Strings.isNullOrEmpty(token) ||
                Strings.isNullOrEmpty(pushId)) {//对参数进行校验
            return ResponseModel.buildParameterError();
        }
        //这里已经拿到用户信息了，不需要多余判断了
        User user = getSelf();
        return bind(user, pushId);

    }

    /**
     * 绑定设备ID的操作
     *
     * @param self
     * @param pushId
     * @return
     */
    private ResponseModel<AccountRspModel> bind(User self, String pushId) {
        User user = UserFactory.bindPushId(self, pushId);
        if (user == null) {//用户为空直接返回error
            return ResponseModel.buildServiceError();
        }
        //已经绑定成功
        AccountRspModel rspModel = new AccountRspModel(user, true);
        return ResponseModel.buildOk(rspModel);
    }


}
























































