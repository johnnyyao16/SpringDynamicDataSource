package com.johnny.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.johnny.test.domain.User;
import com.johnny.test.service.UserService;

@Controller
public class UserController {
    
    @Autowired private UserService userService;
    
    @RequestMapping(value = "/user/slave", method = RequestMethod.GET)
    @ResponseBody
    public User getUserFromSlave() {
        
        return userService.getUserFromSlave("0001");
    }
    
    @RequestMapping(value = "/user/master", method = RequestMethod.GET)
    @ResponseBody
    public User getUserFromMaster() {
        
        return userService.getUserFromMaster("0001");
    }
    
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ResponseBody
    public String saveUser() throws Exception {
        User user = new User();
        user.setTitle("manager");
        user.setUserId("0002");
        user.setUserName("Bob");
        
        userService.saveUser(user);
        
        return "successful!";
    }
}
