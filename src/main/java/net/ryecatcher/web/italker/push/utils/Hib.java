package net.ryecatcher.web.italker.push.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Created by qiujuer
 * on 2017/2/17.
 */
public class Hib {
    // 全局SessionFactory
    private static SessionFactory sessionFactory;

    static {
        // 静态初始化sessionFactory
        init();
    }

    private static void init() {
        // 从hibernate.cfg.xml文件初始化
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            // build 一个sessionFactory
            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            // 错误则打印输出，并销毁
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    /**
     * 获取全局的SessionFactory
     *
     * @return SessionFactory
     */
    public static SessionFactory sessionFactory() {
        return sessionFactory;
    }

    /**
     * 从SessionFactory中得到一个Session会话
     *
     * @return Session
     */
    public static Session session() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 关闭sessionFactory
     */
    public static void closeFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    /**
     * 将具体实现交给上层
     */
    public interface Query<T>{
        T query(Session session);
    }
    public interface QueryOnly{
        void query(Session session);
    }

    /**
     * 只查询，不返回结果
     * @param query
     */
    public static void queryOnly(QueryOnly query){
        //重开一个Session
        Session session =sessionFactory.openSession();
        final  Transaction transaction=session.beginTransaction();

        try{
            query.query(session);
        }catch (Exception e){
            e.printStackTrace();
            //出错回滚事务
            transaction.rollback();
        }finally {
            //无论成功失败都要关闭
            session.close();
        }

    }
    /**
     * 传递一个接口，并将query传递进去
     * 泛型方法，可能需要返回值
     * @param query
     */
    public static  <T> T query(Query<T> query){
        //重开一个Session
        Session session =sessionFactory.openSession();
        final  Transaction transaction=session.beginTransaction();
        T t=null;
        try{
         t= query.query(session);
        }catch (Exception e){
            e.printStackTrace();
            //出错回滚事务
            transaction.rollback();
        }finally {
            //无论成功失败都要关闭
            session.close();
        }
        return  t;
    }



}
