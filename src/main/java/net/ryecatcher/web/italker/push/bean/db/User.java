package net.ryecatcher.web.italker.push.bean.db;


import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * describe:user用户表
 *
 * @Author Zzg
 * @Create 2018-09-03 19:41
 */
@Entity
@Table(name = "TB_USER")
public class User {
    //这是一个主键
    @Id
    @PrimaryKeyJoinColumn
    //主键生成存储的类型为UUID
    @GeneratedValue(generator = "uuid")
    //把uuid的生成器定义为uuid2，uuid2是常规的UUID  toString
    @GenericGenerator(name = "uuid",strategy = "uuid2")
    //不允许更改，不允许为null
    @Column(updatable = false,nullable = false)
    private String id;



    //用户名必须唯一
    @Column(nullable = false,length = 128,unique = true)
    private String name;
    //手机号也必须唯一
    @Column(nullable = false,length = 62,unique = true)
    private String phone;

    @Column(nullable = false)
    private String password;
    //用户头像允许为空，因为第一次新建的时候没有头像
    @Column
    private String portrait;
    @Column
    private String description;
    //性别
    @Column(nullable = false)
    private int sex=0;
    //账户名、电话、token都可以唯一确定一个账号
    @Column(unique = true)
    private String token;
    //用于推送的设备ID，客户端上传
    @Column
    private String pushId;


    //定义为创建时间戳，在创建的时候就已经写入
    @CreationTimestamp
    @Column
    private LocalDateTime createAt=LocalDateTime.now();


    //定义为更新时间戳，在更新的时候就已经写入
    @UpdateTimestamp
    @Column
    private LocalDateTime updateAt=LocalDateTime.now();


    //最后一次收到消息的时间
    @Column
    private LocalDateTime lastReceivedAt=LocalDateTime.now();



    //我关注的人列表方法,所有originid是我的id
    //对应的数据表字段为TB_USER_FOLLOW.originId
    @JoinColumn(name = "originId")
    //定义为懒加载，默认加载User信息的时候，并不查询这个集合
    @LazyCollection(LazyCollectionOption.EXTRA)
//    /1对多，一个人可以关注多个人
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)//与上边这个是配合使用的
    private Set<UserFollow> following=new HashSet<>();



    //关注我的人的列表
    @JoinColumn(name = "targetId")
    //定义为懒加载，默认加载User信息的时候，并不查询这个集合
    @LazyCollection(LazyCollectionOption.EXTRA)
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)//与上边这个是配合使用的
    private Set<UserFollow> followers=new HashSet<>();



    //我所创建的群
    //对应的字段为Group.ownerId
    @JoinColumn(name = "ownerId")
    //懒加载集合方式为尽可能的不加载具体的数据
    //当访问groups.size()仅仅查询数量，不加载具体的Group信息
    //只有当遍历groups的时候才加载
    @LazyCollection(LazyCollectionOption.EXTRA)
    //FetchType.LAZY:懒加载，加载 用户信息时不加载这个集合,跟群里面正好相反
    //必须为懒加载！！！因为群里面个人信息是急加载，个人
    // 如果这里也是急加载，个人信息里就又有群信息需要急加载，就会陷入无限循环，导致内存崩溃！
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Group> groups=new HashSet<>();







    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
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

    public LocalDateTime getLastReceivedAt() {
        return lastReceivedAt;
    }

    public void setLastReceivedAt(LocalDateTime lastReceivedAt) {
        this.lastReceivedAt = lastReceivedAt;
    }

    public Set<UserFollow> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserFollow> following) {
        this.following = following;
    }

    public Set<UserFollow> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<UserFollow> followers) {
        this.followers = followers;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }
}
