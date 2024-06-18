package com.boge.mapper;

import com.boge.pojo.User;

import java.util.List;

public interface UserMapper {

    public List<User> selectUserList();


    public List<User> selectUserByBean(String userName);
}
