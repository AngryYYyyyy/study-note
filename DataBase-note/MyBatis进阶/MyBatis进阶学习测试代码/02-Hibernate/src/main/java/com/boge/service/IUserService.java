package com.boge.service;


import com.boge.model.User;

import java.util.List;

public interface IUserService {

    public List<User> query();

    public User save(User user);
}
