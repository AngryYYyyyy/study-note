package com.boge.mapper;

import com.boge.pojo.User;

import java.util.List;

public interface UserMapper {

    public List<User> selectUserList();


    public User selectUserById(Integer id);
}
