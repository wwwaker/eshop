package com.example.eshop.service;

import com.example.eshop.dao.SysLogDao;
import com.example.eshop.entity.SysLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File;

@Service
@Slf4j
public class SysLogService {
    @Autowired
    private SysLogDao sysLogDao;

//    private static final String LOG_FILE_PATH = System.getProperty("user.dir") + "/logs/eshop.log";
    private static final String LOG_DIR = System.getProperty("user.dir") + "/logs/";


    /**
     * 获取当前运行的日志文件
     */
    public String getCurrentLogFileName() {
        try {
            File dir = new File(LOG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
                return "无日志文件";
            }
            // 获取当前启动的日志
            File[] files = dir.listFiles((d, n) -> n.endsWith(".log"));
            if (files == null || files.length == 0) {
                return "无日志文件";
            }
            Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
            return files[0].getName();
        } catch (Exception e) {
            return "日志文件获取失败";
        }
    }
    /**
     * 获取所有历史日志文件列表
     */
    public List<String> getAllLogFiles() {
        File dir = new File(LOG_DIR);
        if (!dir.exists()) return List.of();
        File[] files = dir.listFiles((d, n) -> n.endsWith(".log"));
        if (files == null) return List.of();
        // 最新文件排在最前
        return Arrays.stream(files)
                .map(File::getName)
                .sorted((a, b) -> b.compareTo(a))
                .collect(Collectors.toList());
    }
    /**
     * 读取指定日志文件
     */
    public List<String> getLogByFileName(String fileName) {
        try {
            if (fileName == null || !fileName.endsWith(".log") || fileName.contains("..")) {
                return List.of("无效的日志文件");
            }
            Path path = Paths.get(LOG_DIR + fileName);
            if (!Files.exists(path)) {
                return List.of("日志文件不存在：" + fileName);
            }
            return Files.readAllLines(path)
                    .stream()
                    .limit(1000)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("读取日志文件异常", e);
            return List.of("读取日志失败");
        }
    }
    // 保存系统日志
    public boolean saveLog(SysLog sysLog) {
        return sysLogDao.insert(sysLog) > 0;
    }

    // 查询所有入库日志
    public List<SysLog> findAllLogs() {
        return sysLogDao.findAll();
    }

    // 按级别查询日志
    public List<SysLog> findLogsByLevel(String level) {
        return sysLogDao.findByLevel(level);
    }

    // 清空数据库日志
    public void clearAllLogs() {
        // 清空数据库日志
        sysLogDao.deleteAll();
        log.info("系统日志已清空");
    }
}