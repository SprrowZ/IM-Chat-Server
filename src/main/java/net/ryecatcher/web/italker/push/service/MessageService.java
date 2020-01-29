package net.ryecatcher.web.italker.push.service;


import com.mchange.util.MEnumeration;
import net.ryecatcher.web.italker.push.bean.api.base.ResponseModel;
import net.ryecatcher.web.italker.push.bean.api.message.MessageCreateModel;
import net.ryecatcher.web.italker.push.bean.card.MessageCard;
import net.ryecatcher.web.italker.push.bean.db.Group;
import net.ryecatcher.web.italker.push.bean.db.Message;
import net.ryecatcher.web.italker.push.bean.db.User;
import net.ryecatcher.web.italker.push.factory.GroupFactory;
import net.ryecatcher.web.italker.push.factory.MessageFactory;
import net.ryecatcher.web.italker.push.factory.PushFactory;
import net.ryecatcher.web.italker.push.factory.UserFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Create by  -SQ-
 * at 2020/1/26 19:30
 *
 * @description:
 */
@Path("/msg")
public class MessageService extends BaseService {
    /**
     * 客户端将消息 发给服务器
     *
     * @param model
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<MessageCard> pushMessage(MessageCreateModel model) {
        if (!MessageCreateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        //消息在数据库中已经存在了
        Message message = MessageFactory.findById(model.getId());
        if (message != null) {
            return ResponseModel.buildOk(new MessageCard(message));
        }
        if (model.getReceiverType() == Message.RECEIVER_TYPE_GROUP) {//群消息
            return pushToGroup(self, model);
        } else {//用户消息
            return pushToUser(self, model);
        }


    }

    /**
     * 发消息给人
     *
     * @param sender
     * @param model
     * @return
     */
    private ResponseModel<MessageCard> pushToUser(User sender, MessageCreateModel model) {
        User receiver = UserFactory.findById(model.getReceiverId());
        if (receiver == null) {
            return ResponseModel.buildNotFoundUserError("Can not find User");
        }
        if (receiver.getId().equalsIgnoreCase(sender.getId())) {
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        //存储数据库
        Message message = MessageFactory.add(sender, receiver, model);
        //将消息通知给接收人
        return buildAndPushResponse(sender, message);

    }

    /**
     * 推送并构建一个返回信息
     *
     * @param sender
     * @param message
     * @return
     */
    private ResponseModel<MessageCard> buildAndPushResponse(User sender, Message message) {
        if (message == null) {//说明存储数据库失败了！
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        //成功存储到本地后推送给接收人
        PushFactory.pushNewMessage(sender, message);
        return ResponseModel.buildOk(new MessageCard(message));
    }

    /**
     * 发送消息到群
     *
     * @param sender
     * @param model
     * @return
     */
    private ResponseModel<MessageCard> pushToGroup(User sender, MessageCreateModel model) {
        Group group = GroupFactory.findById(sender, model);
        if (group == null) {
            return ResponseModel.buildNotFoundUserError("Can not find Group");
        }
        //添加到数据库
        Message message = MessageFactory.add(sender, group, model);
        //走推送的逻辑
        return buildAndPushResponse(sender, message);
    }
}
