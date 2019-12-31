package net.ryecatcher.web.italker.push.factory;

import net.ryecatcher.web.italker.push.bean.db.User;
import net.ryecatcher.web.italker.push.utils.Hib;
import net.ryecatcher.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

/**
 * 用户注册等后台逻辑操作
 */
public class UserFactory {
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
     * 用户注册---注册的操作需要写入数据库，并返回数据库中的User信息
     * @param account
     * @param password
     * @param name
     * @return
     */
    public static User register(String account,String password,String name){
       User user=new User();
       user.setName(name);
       //处理密码
       password=encodePassword(password);
       user.setPassword(password);
       user.setPhone(account);//采用手机号注册
       //数据库操作
        //首先穿件一个会话
        Session session= Hib.session();
        //开启一个事务
        session.beginTransaction();
       try{
           //保存
           session.save(user);
           //提交事务
           session.getTransaction().commit();
       }catch (Exception e){
           session.getTransaction().rollback();//回滚事务
           return null;
       }

        return user;
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
