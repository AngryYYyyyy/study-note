package com.boge;

import com.boge.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HibernateTest {

    /**
     * Hibernate操作案例演示
     * @param args
     */
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        // 默认使用hibernate.cfg.xml
        configuration.configure();
        // 创建Session工厂
        SessionFactory factory = configuration.buildSessionFactory();
        // 创建Session
        Session session = factory.openSession();
        // 获取事务对象
        Transaction transaction = session.getTransaction();
        // 开启事务
        transaction.begin();
        // 把对象添加到数据库中
        User user = new User();
        user.setId(666);
        user.setUserName("hibernate-1");
        user.setRealName("持久层框架");
        session.save(user);
        transaction.commit();
        session.close();
    }
}
