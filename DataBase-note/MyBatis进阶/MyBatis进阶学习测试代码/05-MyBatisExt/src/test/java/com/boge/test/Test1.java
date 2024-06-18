package com.boge.test;

import com.boge.mapper.UserMapper;
import com.boge.mapper.UserMapperExt;
import com.boge.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class Test1 {


    /**
     * MyBatis getMapper 方法的使用
     */
    @Test
    public void test1() throws Exception{
        // 1.获取配置文件
        InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
        // 2.加载解析配置文件并获取SqlSessionFactory对象
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in);
        // 3.根据SqlSessionFactory对象获取SqlSession对象
        SqlSession sqlSession = factory.openSession();
        // 4.通过SqlSession中提供的 API方法来操作数据库
        UserMapperExt mapper = sqlSession.getMapper(UserMapperExt.class);

        List<User> list = mapper.selectUserByName("admin");
        for (User user : list) {
            System.out.println(user);
        }
        // 5.关闭会话
        sqlSession.close();
    }
}
