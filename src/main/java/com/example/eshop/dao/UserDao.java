package com.example.eshop.dao;

import com.example.eshop.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {

    User findById(Long id);

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findAll();

    int insert(User user);

    int update(User user);

    int deleteById(Long id);
}