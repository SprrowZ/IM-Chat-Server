package net.ryecatcher.web.italker.push.service;

import com.google.common.base.Strings;
import net.ryecatcher.web.italker.push.bean.api.account.AccountRspModel;
import net.ryecatcher.web.italker.push.bean.api.account.UpdateInfoModel;
import net.ryecatcher.web.italker.push.bean.api.base.ResponseModel;
import net.ryecatcher.web.italker.push.bean.card.UserCard;
import net.ryecatcher.web.italker.push.bean.db.User;
import net.ryecatcher.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

//127.0.0.1/api/user/..
@Path("/user")
public class UserService extends BaseService {

    /**
     * 用户信息修改接口
     * 返回自己的个人信息
     * @param model
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> update(UpdateInfoModel model) {//直接的token都不需要了，拦截里加了
        //6-11
        User user = getSelf();//能拿到就一定成功了
        user = model.updateToUser(user);
        user = UserFactory.update(user);
        //构造自己的用户信息
        UserCard card = new UserCard(user, true);
        return ResponseModel.buildOk(card);
    }
}
