function clearLogs() {
    if(confirm("⚠️ 确认清空所有日志吗？此操作不可恢复！")){
        window.location.href = "/admin/logs/clear";
    }
}