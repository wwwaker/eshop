package com.example.eshop.service;

import com.example.eshop.entity.User;
import com.example.eshop.dao.UserDao;
import com.example.eshop.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public User findById(Long id) {
        return userDao.findById(id);
    }

    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public boolean register(User user) {
        if (userDao.findByUsername(user.getUsername()) != null) {
            return false;
        }
        user.setRole("USER");
        user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        return userDao.insert(user) > 0;
    }

    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user != null && PasswordUtil.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public boolean update(User user) {
        return userDao.update(user) > 0;
    }

    public boolean deleteById(Long id) {
        return userDao.deleteById(id) > 0;
    }

    public int countTodayNewUsers() {
        return userDao.countTodayNewUsers();
    }
}