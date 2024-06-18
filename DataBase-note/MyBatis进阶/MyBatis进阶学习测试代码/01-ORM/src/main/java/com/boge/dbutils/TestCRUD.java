package com.boge.dbutils;

import com.boge.domain.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestCRUD {

    public static void main(String[] args) throws Exception{
       // new TestCRUD().addUser();
        // new TestCRUD().queryUser();
          new TestCRUD().queryUserUseBeanListHandle();
    }

    /**
     * 添加用户的方法
     */
    public  void addUser() throws SQLException{
        DruidUtils.init();
        QueryRunner queryRunner = DruidUtils.getQueryRunner();
        String sql = "insert into t_user(user_name,real_name)values('aaa','bbbb')";
        int i = queryRunner.update(DruidUtils.getConnection(), sql);
        System.out.println(i);
    }

    /**
     * 查询所有的用户信息
     * @throws Exception
     */
    public void queryUser() throws Exception{
        DruidUtils.init();
        QueryRunner queryRunner = DruidUtils.getQueryRunner();
        String sql = "select * from t_user";
        List<User> list = queryRunner.query(sql, new ResultSetHandler<List<User>>() {

            /**
             * 直接回调
             * @param rs
             * @return
             * @throws SQLException
             */
            @Override
            public List<User> handle(ResultSet rs) throws SQLException {
                List<User> list = new ArrayList<>();
                while(rs.next()){
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUserName(rs.getString("user_name"));
                    user.setRealName(rs.getString("real_name"));
                    user.setPassword(rs.getString("password"));
                    list.add(user);
                }
                return list;
            }
        });
        for (User user : list) {
            System.out.println(user);
        }
    }

    /**
     * 通过ResultHandle的实现类处理查询
     */
    public void queryUserUseBeanListHandle() throws Exception{
        DruidUtils.init();
        QueryRunner queryRunner = DruidUtils.getQueryRunner();
        String sql = "select * from t_user";
        // 不会自动帮助我们实现驼峰命名的转换
        List<User> list = queryRunner.query(sql, new BeanListHandler<User>(User.class));
        for (User user : list) {
            System.out.println(user);
        }
    }

}
