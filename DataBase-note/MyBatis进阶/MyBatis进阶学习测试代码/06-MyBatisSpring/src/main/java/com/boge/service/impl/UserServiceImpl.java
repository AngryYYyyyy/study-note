package com.boge.service.impl;

import com.boge.mapper.UserMapper;
import com.boge.pojo.User;
import com.boge.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> query() {
        return userMapper.query();
    }
}
