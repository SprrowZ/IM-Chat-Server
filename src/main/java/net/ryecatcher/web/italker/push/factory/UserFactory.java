package net.ryecatcher.web.italker.push.factory;

import com.google.common.base.Strings;
import net.ryecatcher.web.italker.push.bean.db.User;
import net.ryecatcher.web.italker.push.utils.Hib;
import net.ryecatcher.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

/**
 * 用户注册等后台逻辑操作
 */
public class UserFactory {

    /**
     * 通过Token查找用户
     * @param token
     * @return
     */
    public static  User findByToken(String token){

        return   Hib.query(session -> (User)session.createQuery("from User where  token=:token")
                .setParameter("token",token)
                .uniqueResult());
    }

    /**
     * 查找手机号是否已经注册
     * @param phone
     * @return
     */
     public static  User findByPhone(String phone){

        return   Hib.query(session -> (User)session.createQuery("from User where  phone=:inPhone")
                .setParameter("inPhone",phone)
                .uniqueResult());
    }

    /**
     * 查找Name是否已经被注册-----昵称相同有错吗...
     * @param name
     * @return
     */
    public static  User findByName(String name){

        return   Hib.query(session -> (User)session.createQuery("from User where  name=:name")
                .setParameter("name",name)
                .uniqueResult());
    }

    /**
     * 使用账户和密码进行登录
     * @param account
     * @param password
     * @return
     */
    public static User login(String account,String password){
      final   String accountStr=account.trim();
        //注册时候保存的密文，登录操作查找也用密文，这样才能匹配的上ORZ
      final   String encodePassword=encodePassword(password);
      //寻找用户
        User user=Hib.query(session -> (User) session.createQuery("from User where phone=:phone and password=:password")
                   .setParameter("phone",accountStr)
                   .setParameter("password",encodePassword)
                   .uniqueResult());
     if (user!=null){
         user=login(user);//登录更新Token存到数据库中
     }
     return user;
    }





    /**
     * 用户注册---注册的操作需要写入数据库，并返回数据库中的User信息
     * @param account
     * @param password
     * @param name
     * @return
     */
    public static User register(String account,String password,String name){
       //处理密码
        password=encodePassword(password);
        User user=createUser(account,password,name);
        if (user!=null){//注册成功，进行登录操作
            user=login(user);
        }

        return user;
    }

    /**
     * 注册部分的新建用户逻辑
     * 使用我们封装的事务操作方法保存对象并返回
     * @param account
     * @param password 加密后的密码
     * @param name
     * @return  返回一个用户
     */
    private static User createUser(String account,String password,String name){
        User user=new User();
        user.setName(name);
        user.setPassword(password);
        user.setPhone(account);//采用手机号注册
        return   Hib.query(session -> {
            session.save(user);//save返回值为Ser，不是user
           return user;
        });
    }

    /**
     * 用户登录操作
     * 本质上就是更新Token
     * @param user User
     * @return User
     */
    private static  User login(User user){
        //主要就是更新Token操作,使用一个随机的UUID值充当Token
        String newToken= UUID.randomUUID().toString();
         //进行一次Base64格式化
        newToken=TextUtil.encodeBase64(newToken);
        user.setToken(newToken);
        //Token也要更新到数据库中
        return Hib.query(session ->{
            session.saveOrUpdate(user);
            return user;
        } );
    }


    /**
     * 绑定pushID,存储到数据库中去
     * @param user
     * @param pushId
     * @return
     */
    public static User bindPushId(User user,String pushId){
        if (Strings.isNullOrEmpty(pushId)){
            return null;
        }

        //第一步，查询是否有其他账户绑定了这个设备
        //取消绑定，避免推送混乱
        //查询的列表不能包括自己
        Hib.queryOnly(session->{
            List<User> userList=session
                    .createQuery("from User  where lower(pushId)=:pushId and id!=:userId")
                    .setParameter("pushId",pushId.toLowerCase())//忽略大小写
                    .setParameter("userId",user.getId())
                    .list();

            for (User u:userList){//这个操作，将前人干掉
                //更新为null
                u.setPushId(null);
                session.saveOrUpdate(u);
            }
        });

        if (pushId.equalsIgnoreCase(user.getPushId())){
            //如果当前需要绑定的设备Id，之前已经绑定过了
            //那么不需要额外绑定
            return user;
        }else{
            //如果当前账户之前的设备id和需要的绑定的不同，那么需要单点登录，让之前的设备退出账户，
            //给之前的设备推送一条消息
            if (Strings.isNullOrEmpty(user.getPushId())){
                //TODO 推送一个退出消息
            }
            //更新新的设备ID
            user.setPushId(pushId);
            return Hib.query(session -> {//绑定了设备ID后，更新用户信息
                session.saveOrUpdate(user);
                return user;
            });


        }



    }



    /**
     * 密码加密
     * @param password
     * @return
     */
    private static  String encodePassword(String password){
        password=password.trim();
        //进行MD5非对称加密
        password= TextUtil.getMD5(password);
        //再进行一次对称加密
        return TextUtil.encodeBase64(password);

    }


}
