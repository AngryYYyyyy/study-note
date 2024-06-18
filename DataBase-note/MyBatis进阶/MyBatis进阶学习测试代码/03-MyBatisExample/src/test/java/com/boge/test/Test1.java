package com.boge.test;

import com.boge.domain.Person;
import com.boge.mapper.UserMapper;
import com.boge.pojo.User;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.reflection.invoker.MethodInvoker;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.awt.print.Pageable;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

public class Test1 {

    /**
     * MyBatis API 的使用
     * MyBatis 在启动的时候会做哪些操作？
     *   1.加载全局配置文件
     *   2.加载映射文件
     *   3.加载的内容存储在了那个Java对象中？ Configuration
     * @throws Exception
     */
    @Test
    public void test1() throws  Exception{
        // 1.获取配置文件
        InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
        // 2.加载解析配置文件并获取SqlSessionFactory对象
        // SqlSessionFactory 的实例我们没有通过 DefaultSqlSessionFactory直接来获取
        // 而是通过一个Builder对象来建造的
        // SqlSessionFactory 生产 SqlSession 对象的  SqlSessionFactory 应该是单例
        // 全局配置文件和映射文件 也只需要在 系统启动的时候完成加载操作
        // 通过建造者模式来 构建复杂的对象  1.完成配置文件的加载解析  2.完成SqlSessionFactory的创建
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in);
        // 3.根据SqlSessionFactory对象获取SqlSession对象
        SqlSession sqlSession = factory.openSession();
        // 4.通过SqlSession中提供的 API方法来操作数据库
        // List<User> list = sqlSession.selectList("com.boge.mapper.UserMapper.selectUserList");
        // 获取接口的代码对象  得到的其实是 通过JDBC代理模式获取的一个代理对象
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        PageHelper.startPage(1,2);
        List<User> list = mapper.selectUserList();
        System.out.println("list.size() = " + list.size());
        System.out.println("-------------");
        System.out.println("list.size() = " + list.size());
        // 5.关闭会话
        sqlSession.close(); // 关闭session  清空一级缓存
        /*sqlSession = factory.openSession();
        mapper = sqlSession.getMapper(UserMapper.class);
        list = mapper.selectUserList();
        System.out.println("list.size() = " + list.size());
        sqlSession.close();*/


    }

    /**
     * MyBatis getMapper 方法的使用
     */
    @Test
    public void test2() throws Exception{
        // 1.获取配置文件
        InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
        // 2.加载解析配置文件并获取SqlSessionFactory对象
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in);
        // 3.根据SqlSessionFactory对象获取SqlSession对象
        SqlSession sqlSession = factory.openSession();
        // 4.通过SqlSession中提供的 API方法来操作数据库
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        /*List<User> list = mapper.selectUserList();
        for (User user : list) {
            System.out.println(user);
        }*/
        mapper.selectUserById(1);
        // 5.关闭会话
        sqlSession.close();
    }



    @Test
    public void test3() throws Exception{
        Reflector reflector = new DefaultReflectorFactory().findForClass(Person.class);

        // 获取到对应的目标对象
        Object obj = reflector.getDefaultConstructor().newInstance();
        // 对ID Field做赋值操作
        Invoker idSetInvoker = reflector.getSetInvoker("id");
        // 对对象中的id field赋值
        idSetInvoker.invoke(obj,new Object[]{6666});
        // 然后获取这个对象中的这个属性的值
        Invoker idGetInvoker = reflector.getGetInvoker("id");
        Object res = idGetInvoker.invoke(obj, null);
        System.out.println(res);
        Method show = reflector.getType().getMethod("show");
        // 执行普通的方法
        Invoker invoker = new MethodInvoker(show);
        System.out.println(invoker.invoke(obj, null));
    }
}
