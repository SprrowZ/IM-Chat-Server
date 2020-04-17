package net.ryecatcher.web.italker.push.service;

import com.google.common.base.Strings;
import net.ryecatcher.web.italker.push.bean.api.base.ResponseModel;
import net.ryecatcher.web.italker.push.bean.api.group.GroupCreateModel;
import net.ryecatcher.web.italker.push.bean.api.group.GroupMemberAddModel;
import net.ryecatcher.web.italker.push.bean.api.group.GroupMemberUpdateModel;
import net.ryecatcher.web.italker.push.bean.card.ApplyCard;
import net.ryecatcher.web.italker.push.bean.card.GroupCard;
import net.ryecatcher.web.italker.push.bean.card.GroupMemberCard;
import net.ryecatcher.web.italker.push.bean.db.Group;
import net.ryecatcher.web.italker.push.bean.db.GroupMember;
import net.ryecatcher.web.italker.push.bean.db.User;
import net.ryecatcher.web.italker.push.factory.GroupFactory;
import net.ryecatcher.web.italker.push.factory.PushFactory;
import net.ryecatcher.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by  -SQ-
 * at 2020/1/29 21:22
 *
 * @description:
 */
@Path("/group")//http://localhost:8080/api/group/..
public class GroupService extends BaseService {
    /**
     * 创建群聊
     *
     * @param model 基本参数
     * @return 群信息
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> create(GroupCreateModel model) {
        if (!GroupCreateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        //创建者
        User creator = getSelf();

        model.getUsers().remove(creator.getId());
        if (model.getUsers().size() == 0) {//等于0说明这个群只有创建者，所以出错
            return ResponseModel.buildParameterError();
        }
        if (GroupFactory.findByName(model.getName()) != null) {
            return ResponseModel.buildHaveNameError();
        }
        //判断群成员信息是否正确
        List<User> users = new ArrayList<>();
        model.getUsers().stream().forEach(userId -> {
            User user = UserFactory.findById(userId);
            if (user != null) {
                users.add(user);
            }
        });
        if (users.size() == 0) {
            return ResponseModel.buildParameterError();
        }

        Group group = GroupFactory.create(creator, model, users);
        if (group == null) {
            return ResponseModel.buildServiceError();
        }
        //拿到创建者信息，规避服务端错误
        GroupMember createMember = GroupFactory.getMember(creator.getId(), group.getId());

        if (createMember == null) {
            return ResponseModel.buildServiceError();
        }
        //拿到群的成员，给所有的群成员发送信息，已经被添加到群的信息
        Set<GroupMember> members = GroupFactory.getMembers(group);
        if (members == null) {
            return ResponseModel.buildServiceError();
        }
        members = members.stream().filter(groupMember -> {
            return !groupMember.getUserId().equalsIgnoreCase(createMember.getId());
        }).collect(Collectors.toSet());
        //开始发起推送
        PushFactory.pushJoinGroup(members);


        return ResponseModel.buildOk(new GroupCard(createMember));
    }


    /**
     * 查找群，没有传递参数就是查找所有的群
     *
     * @param name
     * @return 群信息列表
     */
    @GET
    @Path("/search/{name:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> search(@PathParam("name") @DefaultValue("") String name) {

        User self = getSelf();
        List<Group> groups = GroupFactory.search(name);
        if (groups != null && groups.size() > 0) {
            List<GroupCard> groupCards = groups.stream().map(group -> {
                GroupMember member = GroupFactory.getMember(self.getId(), group.getId());
                return new GroupCard(group, member);
            }).collect(Collectors.toList());
            return ResponseModel.buildOk(groupCards);
        }

        return ResponseModel.buildOk();
    }

    /**
     * 拉取自己所在的群聊;
     * 不传时间,就返回自己最近一段时间的群聊
     * @param date
     * @return
     */
    @GET
    @Path("/list/{date:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> list(@DefaultValue("") @PathParam("date") String date) {
        User self = getSelf();
        LocalDateTime dateTime = null;
        if (!Strings.isNullOrEmpty(date)) {
            try {
                dateTime = LocalDateTime.parse(date);
            } catch (Exception e) {
                e.printStackTrace();
                dateTime = null;
            }
        }
        //拿到一个人的所有GroupMember，代表每个群里的个人信息
        Set<GroupMember> members = GroupFactory.getMembers(self);
        if (members == null || members.size() == 0) {
            return ResponseModel.buildOk();
        }
        final LocalDateTime finalDateTime = dateTime;

        List<GroupCard> groupCards = members.stream()
                .filter(groupMember ->//时间为空，或者在给出的时间之后做过修改的群
                finalDateTime == null || groupMember.getUpdateAt().isAfter(finalDateTime))
                .map(GroupCard::new)
                .collect(Collectors.toList());


        return ResponseModel.buildOk(groupCards);
    }

    /**
     * 获取一个群的信息
     *
     * @param id
     * @return
     */
    @GET
    @Path("/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> getGroup(@PathParam("groupId") String id) {

        if (Strings.isNullOrEmpty(id)){
            return  ResponseModel.buildParameterError();
        }
        User self=getSelf();
        GroupMember member=GroupFactory.getMember(self.getId(),id);
        if (member==null){//想要获取一个群的信息，自己必须是这个群的成员
            return ResponseModel.buildNotFoundUserError(null);
        }


        return ResponseModel.buildOk(new GroupCard(member));
    }

    /**
     * 拉取一个群的所有成员，自己必须是这个群的成员
     *
     * @param groupId
     * @return
     */
    @GET
    @Path("/{groupId}/member")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> members(@PathParam("groupId") String groupId) {
       User self=getSelf();
       Group group=GroupFactory.findById(groupId);
       if (group==null){
           return ResponseModel.buildNotFoundGroupError(null);
       }

       GroupMember selfMember=GroupFactory.getMember(self.getId(),groupId);
       if (selfMember==null){//不是此群的成员
           return ResponseModel.buildNoPermissionError();
       }
       //所有的的成员
       Set<GroupMember> members=GroupFactory.getMembers(group);

       if (members==null){
           return ResponseModel.buildServiceError();
       }
       List<GroupMemberCard> memberCards=members.stream()
               .map(GroupMemberCard::new)
               .collect(Collectors.toList());

        return ResponseModel.buildOk(memberCards);
    }

    /**
     * 群里添加成员,必须是这个群的管理者之一
     *
     * @param groupId
     * @param memberAddModel
     * @return
     */
    @POST
    @Path("/{groupId}/member")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> memberAdd(@PathParam("groupId") String groupId,
                                                          GroupMemberAddModel memberAddModel) {

        // TODO: 2020/2/2 改造成自己申请进群的方法 --即底部的join方法
        if (Strings.isNullOrEmpty(groupId)|| !GroupMemberAddModel.check(memberAddModel)){
            return ResponseModel.buildParameterError();
        }
        User self=getSelf();
        memberAddModel.getUsers().remove(self.getId());
        if (memberAddModel.getUsers().size()==0){//群里只有一个人，也是参数错误
            return ResponseModel.buildParameterError();
        }
        Group group=GroupFactory.findById(groupId);
        if (group==null){//m没找到群，
            return  ResponseModel.buildNotFoundGroupError(null);
        }

        //拿到自己在群里的信息
        GroupMember selfMember=GroupFactory.getMember(self.getId(),groupId);
        //想要加人，就必须是群管理员或群主，普通成员没有权限
        if (selfMember==null || selfMember.getPermissionType()==GroupMember.NOTIFY_LEVEL_NONE){
            return ResponseModel.buildNoPermissionError();
        }
        //拿到已有的群成员
        Set<GroupMember> oldMembers=GroupFactory.getMembers(group);
        //拿到成员的id信息
        Set<String> oldMemberUserIds=oldMembers.stream()
                .map(GroupMember::getGroupId)
                .collect(Collectors.toSet());



        List<User> insertUsers=new ArrayList<>();

        memberAddModel.getUsers().stream().forEach(userId -> {
            User user=UserFactory.findById(userId);
            if (user!=null &&!oldMemberUserIds.contains(userId)) {
                insertUsers.add(user);
            }
        });

        if (insertUsers.size()==0){
            return ResponseModel.buildParameterError();
        }
        //进行添加操作
        Set<GroupMember> insertMembers=GroupFactory.addMembers(group,insertUsers);
        if (insertMembers==null){
            return ResponseModel.buildServiceError();
        }
        //转换
        List<GroupMemberCard> insertCards=insertMembers.stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());
        //通知两部曲
        //1.通知新增的成员，自己被加入群聊
        PushFactory.pushJoinGroup(insertMembers);
        //2.通知老的成员，有新人加入
        PushFactory.pushGroupMemberAdd(oldMembers,insertCards);


        return ResponseModel.buildOk(insertCards);
    }

    /**
     * 更改成员信息，请求的要么是管理员，要么就是请求人自己
     *
     * @param memberId          成员Id，可以查询对应的群和人
     * @param memberUpdateModel
     * @return
     */
    @POST
    @Path("/member/{memberId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupMemberCard> modifyMember(@PathParam("memberId") String memberId,
                                                       GroupMemberUpdateModel memberUpdateModel) {
        return null;
    }

    /**
     * 申请加入一个群，此时会创建一个加入的申请，并写入表；然后给管理员发消息；
     * 管理员统一，就是调用添加成员的接口把对应的用户添加进去
     *
     * @param groupId
     * @return
     */
    @POST
    @Path("/applyJoin/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<ApplyCard> join(@PathParam("groupId") String groupId) {
        return null;
    }

}
