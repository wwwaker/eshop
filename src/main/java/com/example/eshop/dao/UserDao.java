package com.example.eshop.dao;

import com.example.eshop.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserDao {

    User findById(Long id);

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findAll();

    int insert(User user);

    int update(User user);

    int deleteById(Long id);
    
    int countTodayNewUsers();

    List<User> findFiltered(@Param("keyword") String keyword, @Param("role") String role, @Param("offset") int offset, @Param("pageSize") int pageSize);

    int countFiltered(@Param("keyword") String keyword, @Param("role") String role);

}