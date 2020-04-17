package net.ryecatcher.web.italker.push.factory;

import com.google.common.base.Strings;
import net.ryecatcher.web.italker.push.bean.db.User;
import net.ryecatcher.web.italker.push.bean.db.UserFollow;
import net.ryecatcher.web.italker.push.utils.Hib;
import net.ryecatcher.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户注册等后台逻辑操作
 */
public class UserFactory {

    /**
     * 通过ID查找用户
     * @param id
     * @return
     */
    public static User findById(String id) {
        return Hib.query(session -> (User) session.createQuery("from User where  id=:id")
                .setParameter("id", id)
                .uniqueResult());
    }

    /**
     * 通过Token查找用户
     *
     * @param token
     * @return
     */
    public static User findByToken(String token) {

        return Hib.query(session -> (User) session.createQuery("from User where  token=:token")
                .setParameter("token", token)
                .uniqueResult());
    }

    /**
     * 查找手机号是否已经注册
     *
     * @param phone
     * @return
     */
    public static User findByPhone(String phone) {

        return Hib.query(session -> (User) session.createQuery("from User where  phone=:inPhone")
                .setParameter("inPhone", phone)
                .uniqueResult());
    }

    /**
     * 查找Name是否已经被注册-----昵称相同有错吗...
     *
     * @param name
     * @return
     */
    public static User findByName(String name) {

        return Hib.query(session -> (User) session.createQuery("from User where  name=:name")
                .setParameter("name", name)
                .uniqueResult());
    }

    /**
     * 更新用户信息到数据库
     *
     * @param user
     * @return
     */
    public static User update(User user) {
        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
    }


    /**
     * 使用账户和密码进行登录
     *
     * @param account
     * @param password
     * @return
     */
    public static User login(String account, String password) {
        final String accountStr = account.trim();
        //注册时候保存的密文，登录操作查找也用密文，这样才能匹配的上ORZ
        final String encodePassword = encodePassword(password);
        //寻找用户
        User user = Hib.query(session -> (User) session.createQuery("from User where phone=:phone and password=:password")
                .setParameter("phone", accountStr)
                .setParameter("password", encodePassword)
                .uniqueResult());
        if (user != null) {
            user = login(user);//登录更新Token存到数据库中
        }
        return user;
    }


    /**
     * 用户注册---注册的操作需要写入数据库，并返回数据库中的User信息
     *
     * @param account
     * @param password
     * @param name
     * @return
     */
    public static User register(String account, String password, String name) {
        //处理密码
        password = encodePassword(password);
        User user = createUser(account, password, name);
        if (user != null) {//注册成功，进行登录操作
            user = login(user);
        }

        return user;
    }

    /**
     * 注册部分的新建用户逻辑
     * 使用我们封装的事务操作方法保存对象并返回
     *
     * @param account
     * @param password 加密后的密码
     * @param name
     * @return 返回一个用户
     */
    private static User createUser(String account, String password, String name) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setPhone(account);//采用手机号注册
        return Hib.query(session -> {
            session.save(user);//save返回值为Ser，不是user
            return user;
        });
    }

    /**
     * 用户登录操作
     * 本质上就是更新Token
     *
     * @param user User
     * @return User
     */
    private static User login(User user) {
        //主要就是更新Token操作,使用一个随机的UUID值充当Token
        String newToken = UUID.randomUUID().toString();
        //进行一次Base64格式化
        newToken = TextUtil.encodeBase64(newToken);
        user.setToken(newToken);
        //Token也要更新到数据库中
        return update(user);
    }


    /**
     * 绑定pushID,存储到数据库中去
     *
     * @param user
     * @param pushId
     * @return
     */
    public static User bindPushId(User user, String pushId) {
        if (Strings.isNullOrEmpty(pushId)) {
            return null;
        }

        //第一步，查询是否有其他账户绑定了这个设备
        //取消绑定，避免推送混乱
        //查询的列表不能包括自己
        Hib.queryOnly(session -> {
            List<User> userList = session
                    .createQuery("from User  where lower(pushId)=:pushId and id!=:userId")
                    .setParameter("pushId", pushId.toLowerCase())//忽略大小写
                    .setParameter("userId", user.getId())
                    .list();

            for (User u : userList) {//这个操作，将前人干掉
                //更新为null
                u.setPushId(null);
                session.saveOrUpdate(u);
            }
        });

        if (pushId.equalsIgnoreCase(user.getPushId())) {
            //如果当前需要绑定的设备Id，之前已经绑定过了
            //那么不需要额外绑定
            return user;
        } else {
            //如果当前账户之前的设备id和需要的绑定的不同，那么需要单点登录，让之前的设备退出账户，
            //给之前的设备推送一条消息
            if (Strings.isNullOrEmpty(user.getPushId())) {
                //TODO 推送一个退出消息...已完成
                PushFactory.pushLogout(user,user.getPushId());
            }
            //更新新的设备ID
            user.setPushId(pushId);
            return update(user);

        }


    }


    /**
     * 密码加密
     *
     * @param password
     * @return
     */
    private static String encodePassword(String password) {
        password = password.trim();
        //进行MD5非对称加密
        password = TextUtil.getMD5(password);
        //再进行一次对称加密
        return TextUtil.encodeBase64(password);

    }

    /**
     * 获取我的联系人的列表-----java8新特性
     *
     * @param self
     * @return
     */
    public static List<User> contacts(User self) {
        //加载follows不在外部是因为User本身通过session加载，
        //加载一次后失效，必须load再加载一次
        return Hib.query(session -> {
            session.load(self, self.getId());
            Set<UserFollow> follows = self.getFollowing();
            return follows.stream()
                    .map(UserFollow::getTarget)
                    .collect(Collectors.toList());
        });
    }

    /**
     * 关注人的操作
     *
     * @param origin 发起者
     * @param target 被关注人
     * @param alias  被关注人别名
     * @return
     */
    public static User follow(final User origin, final User target, final String alias) {
        UserFollow userFollow = getUserFollow(origin, target);
        if (userFollow != null) {
            return userFollow.getTarget();
        }
        return Hib.query(session -> {
            // TODO: 2020/1/16 load---操作懒加载的数据，需要重新加载
            session.load(origin, origin.getId());
            session.load(target, target.getId());

            UserFollow originFollow = new UserFollow();
            originFollow.setOrigin(origin);
            originFollow.setTarget(target);
            //设置备注，默认他对我没备注
            originFollow.setAlias(alias);

            UserFollow targetFollow = new UserFollow();
            targetFollow.setOrigin(target);
            targetFollow.setTarget(origin);
            //保存数据库
            session.save(originFollow);
            session.save(targetFollow);
            return target;

        });

    }

    /**
     * 查询是否已经关注
     *
     * @param origin
     * @param target
     * @return
     */
    public static UserFollow getUserFollow(final User origin, final User target) {
        return Hib.query(session -> {
            return (UserFollow) session.createQuery("" +
                    "from UserFollow where  originId= :originId and targetId= :targetId ")
                    .setParameter("originId", origin.getId())
                    .setParameter("targetId", target.getId())
                    .setMaxResults(1)//只返回一条有效数据
                    .uniqueResult();
        });
    }

    /**
     * 搜索联系人的实现
     * @param name 查询的name，允许为空
     * @return
     */
    public static List<User> search(String name){
        if (Strings.isNullOrEmpty(name)){
            name="";
        }
        // TODO: 2020/1/18 待了解
        final String searchName="%" +name+"%";
         return  Hib.query(session -> {
             //查询忽略大小写，模糊查询，描述和头像不为空
           return   (List<User>)session.createQuery("from  User  WHERE  lower(name) like :name " +
                    "and portrait is not null  and description is not  null ")
                    .setParameter("name",searchName)
                    .setMaxResults(20)//返回二十条
                    .list();
         });
    }

}
