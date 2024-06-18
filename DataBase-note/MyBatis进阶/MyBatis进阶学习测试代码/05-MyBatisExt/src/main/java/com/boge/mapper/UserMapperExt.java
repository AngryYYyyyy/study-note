package com.boge.mapper;

import com.boge.pojo.User;

import java.util.List;

public interface UserMapperExt extends UserMapper{

    public List<User> selectUserByName(String userName);
}
