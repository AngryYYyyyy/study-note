package com.boge.test;

import com.boge.pojo.User;
import com.boge.service.IUserService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;


public class TestDemo {

    @Test
    public void test1(){
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        IUserService bean = ac.getBean(IUserService.class);
        List<User> list = bean.query();
        for (User user : list) {
            System.out.println(user);
        }

    }
}
