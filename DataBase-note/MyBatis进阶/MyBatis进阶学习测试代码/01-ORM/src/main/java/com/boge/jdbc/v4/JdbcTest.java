package com.boge.jdbc.v4;



import com.boge.domain.User;

import java.util.List;

/**
 * 对JDBC原始操作优化
 *    解决问题
 *       1.重复代码
 *       2.资源管理
 *       3.SQL耦合
 *       4.ResultSet结果集
 */
public class JdbcTest {

    public static void main(String[] args) {
          new JdbcTest().queryUser();
       //new JdbcTest().addUser();
    }

    /**
     *
     * 通过JDBC查询用户信息
     */
    public void queryUser(){
        try {
            String sql = "SELECT id,user_name,real_name,password,age,d_id from t_user where id = ?";
            List<User> list = DBUtils.query(sql, User.class,1);
            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过JDBC实现添加用户信息的操作
     */
    public void addUser(){
        String sql = "INSERT INTO T_USER(user_name,real_name,password,age,d_id)values(?,?,?,?,?)";
        try {
            DBUtils.update(sql,"wangwu","王五","111",22,1001);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
