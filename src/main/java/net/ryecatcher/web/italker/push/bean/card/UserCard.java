package net.ryecatcher.web.italker.push.bean.card;

import com.google.gson.annotations.Expose;
import net.ryecatcher.web.italker.push.bean.db.User;

import java.time.LocalDateTime;

/**
 * describe:用户注册时返回的信息
 *
 * @Author
 * @Create
 */
public class UserCard {
    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private String portrait;
    @Expose
    private String desc;
    @Expose
    private int sex=0;

   //用户信息最后的更新时间
    @Expose
    private LocalDateTime updateAt =LocalDateTime.now();
   //用户粉丝的数量
    @Expose
    private int following;
    //我与当前User的关系状态，是否已经关注了这个人
    @Expose
    private boolean isFollow;
    //手机号也是用户的账号
    @Expose
    private String phone;

    /**
     * 通过User来赋值部分信息给UserCard
     * @param user
     */
    public UserCard (final User user){
      this(user,false);
    }
    public UserCard (final User user,boolean isFollow){

        this.isFollow=isFollow;
        this.id=user.getId();
        this.name=user.getName();
        this.phone=user.getPhone();
        this.portrait=user.getPortrait();
        this.desc=user.getDescription();
        this.sex=user.getSex();
        this.updateAt =user.getUpdateAt();
        // TODO: 2020/1/1 获取关注人和粉丝的数量
    }






    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public boolean getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(boolean isFollow) {
        this.isFollow = isFollow;
    }
}
