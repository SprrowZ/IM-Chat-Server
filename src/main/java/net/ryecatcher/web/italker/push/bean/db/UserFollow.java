package net.ryecatcher.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * describe:用户关系表
 *y用于用户直接进行好友关系的实现
 * @Author Zzg
 * @Create 2018-09-03 21:12
 */
@Entity
@Table(name = "TB_USER_FOLLOW")
public class UserFollow {
    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid",strategy = "uuid2")
    @Column
    private String id;

    //定义一个发起人，你关注某人，这里就是你
    //多对1->你可以关注很多人，每一次关注都是一条记录
    //你可以创建很多个关注的信息，所以是多对1
    //这里的多对一指的是：User对应多一个UserFollow
    //optional不可选，必须存储，一条关注记录一定要有一个"你"
    //不要存用户全部信息，只需要存个ID即可
    @ManyToOne(optional = false)//多个UserFollow对应一个User
    //定义关联的表字段名为originId，对应一个User.id
    @JoinColumn(name = "originId")
    private User origin;
    //可以通过ID查找关注人，同理被关注人
    @Column(nullable = false,updatable = false,insertable = false)
    private String originId;
    //定义关注的目标，你关注的人
    //也是多对1，你可以被很多人关注，每次一关注都是一条记录
    //所有就是 多个UserFollow对应一个User的情况
    //
    @ManyToOne(optional = false)
    //定义关联的表字段名为targetId，对应的是User.id
    @JoinColumn(name = "targetId")
    private User target;
    //可以通过ID查找关注人，同理被关注人
    @Column(nullable = false,updatable = false,insertable = false)
    private String targetId;
    //对关注的人起个备注名,可以为空

    @Column
    private String alias;
    //定义为创建时间戳，在创建的时候就已经写入
    @CreationTimestamp
    @Column
    private LocalDateTime createAt=LocalDateTime.now();

    //定义为更新时间戳，在更新的时候就已经写入
    @UpdateTimestamp
    @Column
    private LocalDateTime updateAt=LocalDateTime.now();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOrigin() {
        return origin;
    }

    public void setOrigin(User origin) {
        this.origin = origin;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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
}
