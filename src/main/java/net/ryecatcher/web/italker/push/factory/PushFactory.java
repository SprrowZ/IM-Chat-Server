package net.ryecatcher.web.italker.push.factory;

import com.google.common.base.Strings;
import com.google.gson.internal.$Gson$Preconditions;
import net.ryecatcher.web.italker.push.bean.api.base.PushModel;
import net.ryecatcher.web.italker.push.bean.card.GroupMemberCard;
import net.ryecatcher.web.italker.push.bean.card.MessageCard;
import net.ryecatcher.web.italker.push.bean.card.UserCard;
import net.ryecatcher.web.italker.push.bean.db.*;
import net.ryecatcher.web.italker.push.utils.Hib;
import net.ryecatcher.web.italker.push.utils.PushDispatcher;
import net.ryecatcher.web.italker.push.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Create by  -SQ-
 * at 2020/1/26 21:11
 *
 * @description:
 */
public class PushFactory {
    /**
     * 推送消息
     *
     * @param sender
     * @param message
     */
    public static void pushNewMessage(User sender, Message message) {
        if (sender == null || message == null) return;
        MessageCard card = new MessageCard(message);
        //要发送的消息实体
        String entity = TextUtil.toJson(card);
        //发送者
        PushDispatcher dispatcher = new PushDispatcher();
        if (message.getGroup() == null && Strings.isNullOrEmpty(message.getGroupId())) {
            //给朋友发信息
            User receiver = UserFactory.findById(message.getReceiverId());
            if (receiver == null) return;

            PushHistory history = new PushHistory();
            history.setEntityType(PushModel.ENTITY_TYPE_MESSAGE);
            history.setEntity(entity);
            history.setReceiver(receiver);
            //接收者的设备id
            history.setReceiverPushId(receiver.getPushId());

            //推送的真实Model
            PushModel pushModel = new PushModel();
            pushModel.add(history.getEntityType(), history.getEntity());
            dispatcher.add(receiver, pushModel);
            //将推送记录保存到数据库
            Hib.queryOnly(session -> session.save(history));


        } else {//给群成员发消息

            Group group = message.getGroup();
            if (group == null) {//延迟加载，可能为空
                group = GroupFactory.findById(message.getGroupId());
            }
            Set<GroupMember> members = GroupFactory.getMembers(group);
            if (members == null || members.size() == 0) return;
            //过滤掉自己
            members = members.stream().filter(groupMember -> !groupMember.getUserId()
                    .equalsIgnoreCase(sender.getId()))
                    .collect(Collectors.toSet());

            List<PushHistory> histories = new ArrayList<>();
            addGroupMembersPushModel(dispatcher,
                    histories,
                    members,
                    entity,
                    PushModel.ENTITY_TYPE_MESSAGE);
            //将群里每个人消息都存到数据库中
            Hib.queryOnly(session -> {
                histories.stream().forEach(history ->
                        session.saveOrUpdate(history)
                );
            });

        }
        //推送
        dispatcher.submit();
    }

    /**
     * 给群成员构建构建一个消息，把消息存储到数据库的历史记录中，每个人每条消息都是一个记录
     *
     * @param dispatcher
     * @param histories
     * @param members
     * @param entity
     * @param entityTypeMessage
     */
    private static void addGroupMembersPushModel(PushDispatcher dispatcher, List<PushHistory> histories,
                                                 Set<GroupMember> members, String entity, int entityTypeMessage) {
        members.stream().forEach(member -> {
            User receiver = member.getUser();
            if (receiver == null) return;
            PushHistory history = new PushHistory();
            history.setEntityType(entityTypeMessage);
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());
            histories.add(history);
            //构建一个消息Model
            PushModel pushModel = new PushModel();
            pushModel.add(history.getEntityType(), history.getEntity());
            //添加到一个发送者中
            dispatcher.add(receiver, pushModel);

        });
    }

    /**
     * 给群成员发送被添加到群的消息
     * @param members
     */
    public static void pushJoinGroup(Set<GroupMember> members) {
        //个推中的发送者
        PushDispatcher dispatcher=new PushDispatcher();
        //发送的消息要存到历史表中
        List<PushHistory> histories=new ArrayList<>();
        members.stream().forEach(member->{
            User receiver=member.getUser();
            if (receiver==null) return;

            GroupMemberCard memberCard=new GroupMemberCard(member);
            String entity=TextUtil.toJson(memberCard);
            PushHistory history=new PushHistory();
            history.setEntityType(PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS);
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());
            histories.add(history);
            //构建一个消息Model
            PushModel pushModel=new PushModel();
            pushModel.add(history.getEntityType(),history.getEntity());
            dispatcher.add(receiver,pushModel);
        });
        Hib.queryOnly(session -> {
            histories.stream().forEach(history->{
                session.saveOrUpdate(histories);
            });
        });
        //不要忘了提交，否则不推送
        dispatcher.submit();
    }

    /**
     * 通知老成员，有新人加入
     * @param oldMembers
     * @param insertCards
     */
    public static void pushGroupMemberAdd(Set<GroupMember> oldMembers, List<GroupMemberCard> insertCards) {

        PushDispatcher dispatcher=new PushDispatcher();
        List<PushHistory> histories=new ArrayList<>();
        String entity= TextUtil.toJson(insertCards);
        //给每个老成员推送一条消息，消息内容为新增用户集合
        addGroupMembersPushModel(dispatcher,histories,oldMembers,
                entity,PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS);
        //消息存到推送历史表
        Hib.queryOnly(session -> {
            histories.stream().forEach(history -> {
                session.saveOrUpdate(histories);
            });
        });
      dispatcher.submit();
    }

    /**
     * 推送账户退出消息
     * @param receiver  接收者
     * @param pushId    这个时刻的接受者的设备ID，比如某某设备已经登录此账号，单点登录？？
     */
    public static void pushLogout(User receiver, String pushId) {
        //存到推送历史表
        PushHistory history=new PushHistory();
        history.setEntityType(PushModel.ENTITY_TYPE_LOGOUT);
        history.setEntity("Account Logout");
        history.setReceiver(receiver);
        history.setReceiverPushId(pushId);
        Hib.queryOnly(session -> session.saveOrUpdate(history));
        //推送退出账号的消息
        PushDispatcher dispatcher=new PushDispatcher();
        PushModel pushModel=new PushModel()
                .add(history.getEntityType(),history.getEntity());
        //添加并提交到第三方推送
        dispatcher.add(receiver,pushModel);
        dispatcher.submit();



    }

    /**
     * 推送给我关注的人一条消息，内容是我的信息
     * @param receiver
     * @param userCard
     */
    public static void pushFollow(User receiver, UserCard userCard) {
        //一定已经相互关注了
        userCard.setFollow(true);
        String entity=TextUtil.toJson(userCard);
        //存到推送消息历史表中
        PushHistory history=new PushHistory();
        history.setEntityType(PushModel.ENTITY_TYPE_ADD_FRIEND);
        history.setEntity(entity);
        history.setReceiver(receiver);
        history.setReceiverPushId(receiver.getPushId());

        Hib.queryOnly(session -> {
            session.save(history);
        });

        PushDispatcher dispatcher=new PushDispatcher();
        PushModel pushModel=new PushModel()
                .add(history.getEntityType(),history.getEntity());
        dispatcher.add(receiver,pushModel);
        dispatcher.submit();
    }
}
