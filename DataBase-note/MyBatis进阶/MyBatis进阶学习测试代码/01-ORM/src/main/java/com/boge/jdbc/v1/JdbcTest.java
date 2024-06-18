package com.boge.jdbc.v1;

import com.boge.domain.User;
import java.sql.*;

/**
 * Jdbc的基本操作
 */
public class JdbcTest {

    public static void main(String[] args) {
         new JdbcTest().queryUser();
        // new JdbcTest().addUser();
    }

    /**
     *
     * 通过JDBC查询用户信息
     */
    public void queryUser(){
        Connection conn = null;
        Statement stmt = null;
        User user = new User();
        try {
            // 注册 JDBC 驱动
            // Class.forName("com.mysql.cj.jdbc.Driver");
            // 打开连接
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mybatis-vip?characterEncoding=utf-8&serverTimezone=UTC", "root", "123456");
            // 执行查询
            stmt = conn.createStatement();
            String sql = "SELECT id,user_name,real_name,password,age,d_id from t_user where id = 1";
            ResultSet rs = stmt.executeQuery(sql);
            // 获取结果集
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String userName = rs.getString("user_name");
                String realName = rs.getString("real_name");
                String password = rs.getString("password");
                Integer age = rs.getInt("age");
                Integer did = rs.getInt("d_id");
                user.setId(id);
                user.setUserName(userName);
                user.setRealName(realName);
                user.setPassword(password);
                user.setAge(age);
                user.setDId(did);

                System.out.println(user);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /**
     * 通过JDBC实现添加用户信息的操作
     */
    public void addUser(){
        Connection conn = null;
        Statement stmt = null;
        try {
            // 注册 JDBC 驱动
            // Class.forName("com.mysql.cj.jdbc.Driver");
            // 打开连接
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mybatis-vip?characterEncoding=utf-8&serverTimezone=UTC", "root", "123456");
            // 执行查询
            stmt = conn.createStatement();
            String sql = "INSERT INTO T_USER(user_name,real_name,password,age,d_id)values('admin','管理员','111',22,1001)";
            int i = stmt.executeUpdate(sql);
            System.out.println("影响的行数:" + i);
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
