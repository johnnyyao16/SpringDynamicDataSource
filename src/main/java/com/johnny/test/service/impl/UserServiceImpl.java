package com.johnny.test.service.impl;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.johnny.test.annotation.DataSource;
import com.johnny.test.domain.User;
import com.johnny.test.mapper.UserMapper;
import com.johnny.test.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired private UserMapper userMapper;
    
    @DataSource("slave")
    public User getUserFromSlave(String userId) {
        // TODO Auto-generated method stub
        return userMapper.selectUser(userId);
    }
    
    @DataSource("slave")
    public void saveUser(User user) {
        // TODO Auto-generated method stub
        userMapper.insertUser(user);
        Assert.assertTrue(getUserFromMaster(user.getUserId()) != null);
        //throw new RuntimeException();
    }

    @Override
    @DataSource("master")
    public User getUserFromMaster(String userId) {
        // TODO Auto-generated method stub
        return userMapper.selectUser(userId);
    }

    
}
