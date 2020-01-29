package net.ryecatcher.web.italker.push.factory;

import com.google.common.base.Strings;
import net.ryecatcher.web.italker.push.bean.api.base.PushModel;
import net.ryecatcher.web.italker.push.bean.card.MessageCard;
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
}
