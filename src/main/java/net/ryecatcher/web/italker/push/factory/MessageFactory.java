package net.ryecatcher.web.italker.push.factory;

import net.ryecatcher.web.italker.push.bean.api.message.MessageCreateModel;
import net.ryecatcher.web.italker.push.bean.db.Group;
import net.ryecatcher.web.italker.push.bean.db.Message;
import net.ryecatcher.web.italker.push.bean.db.User;
import net.ryecatcher.web.italker.push.utils.Hib;

/**
 * Create by  -SQ-
 * at 2020/1/26 19:30
 *消息实体保存的类
 * @description:
 */
public class MessageFactory {
    /**
     * 从数据库中查找一条消息
     * @param id
     * @return
     */
    public static Message findById(String id){
    return Hib.query(session ->
        session.get(Message.class,id)
     );
}

public  static Message add(User sender, User receiver, MessageCreateModel model){
        Message message =new Message(sender,receiver,model);
        return save(message);
}

public static  Message add(User sender, Group group,MessageCreateModel model){
      Message message=new Message(sender,group,model);
      return save(message);
}

    /**
     * 将消息存储到数据库中
     * @param message
     * @return
     */
    private static Message save(Message message){
        return Hib.query(session -> {
            session.save(message);
            // TODO: 2020/1/26 再查一遍所谓何？
            session.flush();
            session.refresh(message);
            return  message;
        });
}



}
