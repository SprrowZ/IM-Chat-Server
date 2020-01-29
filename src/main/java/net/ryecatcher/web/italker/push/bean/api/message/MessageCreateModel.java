package net.ryecatcher.web.italker.push.bean.api.message;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;
import net.ryecatcher.web.italker.push.bean.api.account.UpdateInfoModel;
import net.ryecatcher.web.italker.push.bean.db.Message;

/**
 * Create by  -SQ-
 * at 2020/1/26 19:32
 *
 * @description:
 */
public class MessageCreateModel {
    @Expose
    private String id;
    @Expose
    private String content;
    @Expose
    private String attach;
    //消息类型
    @Expose
    private int type = Message.TYPE_STR;
    @Expose
    private String senderId;
    @Expose
    private String receiverId;


    //接收者类型，可能是群，也可能是人
    @Expose
    private int receiverType = Message.RECEIVER_TYPE_NONE;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(int receiverType) {
        this.receiverType = receiverType;
    }

    public static boolean check(MessageCreateModel model) {
        // Model 不允许为null，
        // 并且只需要具有一个及其以上的参数即可
        return model != null
                && (!Strings.isNullOrEmpty(model.id) ||
//                !Strings.isNullOrEmpty(model.senderId) || //通过token已经能确定身份了
                !Strings.isNullOrEmpty(model.receiverId) ||
                !Strings.isNullOrEmpty(model.content))
                && (model.receiverType == Message.RECEIVER_TYPE_NONE ||
                model.receiverType == Message.RECEIVER_TYPE_GROUP)
                && (model.type == Message.TYPE_STR ||
                model.type == Message.TYPE_AUDIO ||
                model.type == Message.TYPE_FILE ||
                model.type == Message.TYPE_PIC);
    }
}
