package com.johnny.test.mapper;

import org.apache.ibatis.annotations.Param;

import com.johnny.test.domain.User;

public interface UserMapper {
    
    User selectUser(@Param("userId") String userId);
    
    void insertUser(User user);
}
