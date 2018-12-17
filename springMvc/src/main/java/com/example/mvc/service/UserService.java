package com.example.mvc.service;

import com.example.mvc.dao.UserDao;
import com.example.mvc.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public List<User> getAll(){
        return (List<User>) userDao.findAll();
    }
}
