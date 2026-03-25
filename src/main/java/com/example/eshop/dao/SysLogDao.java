package com.example.eshop.dao;

import com.example.eshop.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SysLogDao {
    // 插入日志
    int insert(SysLog sysLog);
    // 查询所有日志
    List<SysLog> findAll();
    // 按级别查询日志
    List<SysLog> findByLevel(String logLevel);
    // 清空日志
    void deleteAll();
}