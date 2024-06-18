package com.boge.springjdbc;

import com.boge.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate template;

    public void addUser(){
        int count = template.update("insert into t_user(user_name,real_name)values(?,?)","anan","安安老师");
        System.out.println("count = " + count);
    }



    public void query1(){
        String sql = "select * from t_user";
        List<User> list = template.query(sql, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUserName(rs.getString("user_name"));
                user.setRealName(rs.getString("real_name"));
                return user;
            }
        });
        for (User user : list) {
            System.out.println(user);
        }
    }


    public void query2(){
        String sql = "select * from t_user";
        List<User> list = template.query(sql, new BeanPropertyRowMapper<>(User.class));
        for (User user : list) {
            System.out.println(user);
        }
    }

}
