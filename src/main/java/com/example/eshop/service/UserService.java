package com.example.eshop.service;

import com.example.eshop.entity.User;
import com.example.eshop.mapper.UserMapper;
import com.example.eshop.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findById(Long id) {
        return userMapper.findById(id);
    }

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public List<User> findAll() {
        return userMapper.findAll();
    }

    public boolean register(User user) {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return false;
        }
        user.setRole("USER");
        user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        return userMapper.insert(user) > 0;
    }

    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && PasswordUtil.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public boolean update(User user) {
        return userMapper.update(user) > 0;
    }

    public boolean deleteById(Long id) {
        return userMapper.deleteById(id) > 0;
    }
}
