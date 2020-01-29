package net.ryecatcher.web.italker.push.service;

import net.ryecatcher.web.italker.push.bean.api.base.ResponseModel;
import net.ryecatcher.web.italker.push.bean.api.group.GroupCreateModel;
import net.ryecatcher.web.italker.push.bean.api.group.GroupMemberAddModel;
import net.ryecatcher.web.italker.push.bean.api.group.GroupMemberUpdateModel;
import net.ryecatcher.web.italker.push.bean.card.ApplyCard;
import net.ryecatcher.web.italker.push.bean.card.GroupCard;
import net.ryecatcher.web.italker.push.bean.card.GroupMemberCard;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

/**
 * Create by  -SQ-
 * at 2020/1/29 21:22
 *
 * @description:
 */
@Path("/group")
public class GroupService extends BaseService {
    /**
     * 创建群聊
     * @param model 基本参数
     * @return  群信息
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> create(GroupCreateModel model){
        return null;
    }

    /**
     * 查找群，没有传递参数就是查找所有的群
     * @param name
     * @return 群信息列表
     */
    @GET
    @Path("/search/{name:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> search(@PathParam("name") @DefaultValue("") String name){
        return  null;
    }

    /**
     * 拉取自己所在的群聊，不传参；就返回自己最近一段时间的群聊
     * @param date
     * @return
     */
    @GET
    @Path("/list/{date:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> list(@DefaultValue("") @PathParam("date") String date){
        return  null;
    }

    /**
     * 获取一个群的信息
     * @param id
     * @return
     */
    @GET
    @Path("/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> getGroup(@PathParam("groupId") String id){
      return null;
    }

    /**
     * 拉取一个群的所有成员，自己必须是这个群的成员
     * @param groupId
     * @return
     */
    @GET
    @Path("/{groupId}/members")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> members(@PathParam("groupId") String groupId){
         return null;
    }

    /**
     * 群里添加成员,必须是这个群的管理者之一
     * @param groupId
     * @param memberAddModel
     * @return
     */
    @POST
    @Path("/add/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> memberAdd(@PathParam("groupId") String groupId,
                                                          GroupMemberAddModel memberAddModel){
        return  null;
    }

    /**
     * 更改成员信息，请求的要么是管理员，要么就是请求人自己
     * @param memberId  成员Id，可以查询对应的群和人
     * @param memberUpdateModel
     * @return
     */
    @POST
    @Path("/member/{memberId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupMemberCard> modifyMember(@PathParam("memberId") String memberId,
                                                       GroupMemberUpdateModel memberUpdateModel){
        return null;
    }

    /**
     * 申请加入一个群，此时会创建一个加入的申请，并写入表；然后给管理员发消息；
     * 管理员统一，就是调用添加成员的接口把对应的用户添加进去
     * @param groupId
     * @return
     */
    @POST
    @Path("/applyJoin/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<ApplyCard> join(@PathParam("groupId") String groupId){
        return  null;
    }

}
