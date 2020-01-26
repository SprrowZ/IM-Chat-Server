package net.ryecatcher.web.italker.push.service;

import com.google.common.base.Strings;


import net.ryecatcher.web.italker.push.bean.api.account.UpdateInfoModel;
import net.ryecatcher.web.italker.push.bean.api.base.ResponseModel;
import net.ryecatcher.web.italker.push.bean.card.UserCard;
import net.ryecatcher.web.italker.push.bean.db.User;
import net.ryecatcher.web.italker.push.factory.UserFactory;


import javax.ws.rs.*;

import javax.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

//127.0.0.1/api/user/..
@Path("/user")
public class UserService extends BaseService {

    /**
     * 用户信息修改接口
     * 返回自己的个人信息
     *
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

    /**
     * 获取关注人的信息---java8新特性
     *
     * @return
     */
    @GET
    @Path("/contact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> contact() {
        User self = getSelf();
        //拿到我的联系人
        List<User> users = UserFactory.contacts(self);
        List<UserCard> userCards = users.stream()
                .map(user -> {
                    return new UserCard(user, true);
                }).collect(Collectors.toList());
        return ResponseModel.buildOk(userCards);
    }

    /**
     * 关注某人--其实是双方都关注
     *
     * @param followId
     * @return
     */
    @PUT
    @Path("/follow/{followId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> follow(@PathParam("followId") String followId) {
        User self = getSelf();
        if (self.getId().equalsIgnoreCase(followId) || Strings.isNullOrEmpty(followId)) {//关注人是自己或空
            return ResponseModel.buildParameterError();
        }
        //找到我也关注的人
        User followUser = UserFactory.findById(followId);
        if (followUser == null) {//没有找到联系人
            return ResponseModel.buildNotFoundUserError(null);
        }
        //备注默认没有
        followUser = UserFactory.follow(self, followUser, null);
        if (followUser == null) {
            //关注失败，返回服务器异常
            return ResponseModel.buildServiceError();
        }
        //通知我关注的人，我关注了他
        // TODO: 2020/1/17  

        return ResponseModel.buildOk(new UserCard(followUser, true));
    }

    /**
     * 获取某人的信息
     * @param id
     * @return
     */
    @GET
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> getUser(@PathParam("id") String id) {
        if (Strings.isNullOrEmpty(id)) {
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        if (self.getId().equalsIgnoreCase(id)) {//自己默认是关注自己的
            return ResponseModel.buildOk(new UserCard(self, true));
        }

        User user = UserFactory.findById(id);
        if (user == null) {
            return ResponseModel.buildNotFoundUserError(null);
        }
        boolean isFollow = UserFactory.getUserFollow(self, user) != null;
        return ResponseModel.buildOk(new UserCard(user, isFollow));
    }

    /**
     * 搜索人的接口，为了简化分页，只返回20条
     * @param name
     * @return 查询到的用户集合，name为空，则返回最近的用户
     */
    @GET
    @Path("/search/{name:(.*)?}")//正则
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> search(@DefaultValue("") @PathParam("name") String name){
        User self=getSelf();
        //先查询数据
        List<User> searchUsers=UserFactory.search(name);
        //把查询的人封装为UserCard，判断这些人中是否已经有我已经关注的人
        //如果有，则返回的关注状态中应该已经设置好状态
        List<User> contacts=UserFactory.contacts(self);
        // TODO: 2020/1/18 耗时--两个循环 
        List<UserCard> userCards=searchUsers.stream()
                .map(user -> {
                    //判断这个人是否在我的联系人中
                    boolean isFollow=user.getId().equalsIgnoreCase(self.getId())
                            //进行联系人的任意匹配
                            || contacts.stream().anyMatch(contactUser->
                            contactUser.getId().equalsIgnoreCase(user.getId()));

                    return new UserCard(user,isFollow);
                }).collect(Collectors.toList());
        //
        return ResponseModel.buildOk(userCards);
    }

}
