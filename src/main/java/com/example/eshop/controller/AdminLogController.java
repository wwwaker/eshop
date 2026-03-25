package com.example.eshop.controller;

import com.example.eshop.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

/**
 * 管理员日志管理控制器
 */
@Controller
@RequestMapping("/admin/logs")
public class AdminLogController {
    @Autowired
    private SysLogService sysLogService;

    // 日志管理首页
    @GetMapping("")
    public String logPage(Model model,
                          @RequestParam(required = false) String fileName,
                          @RequestParam(required = false) String level) {

        // 实时日志
        String targetFile = (fileName == null) ? sysLogService.getCurrentLogFileName() : fileName;

        model.addAttribute("realTimeLogs", sysLogService.getLogByFileName(targetFile));
        model.addAttribute("allLogFiles", sysLogService.getAllLogFiles());
        model.addAttribute("currentFileName", targetFile);

        // 操作日志
        List<com.example.eshop.entity.SysLog> operateLogs;
        if (level != null && !level.isEmpty()) {
            operateLogs = sysLogService.findLogsByLevel(level);
        } else {
            operateLogs = sysLogService.findAllLogs();
        }

        model.addAttribute("operateLogs", operateLogs);
        model.addAttribute("currentLevel", level);
        return "admin/logs";
    }

    @GetMapping("/clear")
    public String clearLogs() {
        sysLogService.clearAllLogs();
        // 清空后重定向回日志页面
        return "redirect:/admin/logs";
    }
}