package com.example.eshop.controller;

import com.example.eshop.service.SysLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequestMapping("/admin/logs")
public class AdminLogController {

    private final SysLogService sysLogService;
    private static final int PAGE_SIZE = 15;

    public AdminLogController(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    @GetMapping("")
    public String logPage(Model model,
                          @RequestParam(required = false) String fileName,
                          @RequestParam(required = false) String level,
                          @RequestParam(required = false) String searchKeyword,
                          @RequestParam(required = false, defaultValue = "0") int page) {

        String targetFile = (fileName == null) ? sysLogService.getCurrentLogFileName() : fileName;

        model.addAttribute("realTimeLogs", sysLogService.getLogByFileName(targetFile));
        model.addAttribute("allLogFiles", sysLogService.getAllLogFiles());
        model.addAttribute("currentFileName", targetFile);

        List<com.example.eshop.entity.SysLog> operateLogs = sysLogService.findFiltered(level, searchKeyword, page, PAGE_SIZE);
        int totalItems = sysLogService.countFiltered(level, searchKeyword);
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);

        model.addAttribute("operateLogs", operateLogs);
        model.addAttribute("currentLevel", level);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        return "admin/logs";
    }

    @GetMapping("/clear")
    public String clearLogs() {
        sysLogService.clearAllLogs();
        return "redirect:/admin/logs";
    }
}
