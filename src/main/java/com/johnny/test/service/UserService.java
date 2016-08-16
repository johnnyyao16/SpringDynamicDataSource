package com.johnny.test.service;

import com.johnny.test.domain.User;

public interface UserService {
    
    User getUserFromSlave(String userId);
    
    User getUserFromMaster(String userId);
    
    void saveUser(User user);
}
