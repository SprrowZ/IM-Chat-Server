package net.ryecatcher.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * describe:消息表
 *
 * @Author
 * @Create
 */
@Entity
@Table(name = "TB_MESSAGE")
public class Message {
    public static final int TYPE_STR = 1;//字符串类型
    public static final int TYPE_PIC = 2;//图片类型
    public static final int TYPE_FILE = 3;//文件类型
    public static final int TYPE_AUDIO = 4;//语音
    //这是一个主键
    @Id
    @PrimaryKeyJoinColumn
    //主键生成存储的类型为UUID
    //这里不自动生成UUID，Id由代码写入，由客户端负责生成
    //主要是为了便面复杂的服务器和客户端的映射关系
    // @GeneratedValue(generator = "uuid")
    //把uuid的生成器定义为uuid2，uuid2是常规的UUID  toString
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //不允许更改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;


    //内容不允许为空，类型为text
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;


    //附件可以为空
    @Column()
    private String attach;


    //消息类型
    @Column(nullable = false)
    private int type;


    //定义为创建时间戳，在创建的时候就已经写入
    @CreationTimestamp
    @Column
    private LocalDateTime createAt = LocalDateTime.now();



    //定义为更新时间戳，在更新的时候就已经写入
    @UpdateTimestamp
    @Column
    private LocalDateTime updateAt = LocalDateTime.now();


   //发送者，不为空，多个消息对应一个sender
    @JoinColumn(name = "senderId")
    @ManyToOne(optional = false)//跟nullable为false一样
    private User  sender;


    //接收ID，这样就可以懒加载，只加载用户的id而不是全部信息
    //仅仅是为了对应sender的数据库字段senderId，
    //不允许手动的更新或者插入
    @Column(nullable = false,updatable = false,insertable = false)
    private String senderId;

   //接受者可以为空，因为有群
    //多个消息对应一个接受者
    @ManyToOne
    @JoinColumn(name = "receiverId")
    private User receiver;


    @Column(updatable = false,insertable = false)
    private String receiverId;


    @ManyToOne
    @JoinColumn(name = "groupId")
    private Group group;
    @Column(updatable = false,insertable = false)
    private String groupId;



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

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
