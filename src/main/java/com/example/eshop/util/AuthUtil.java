package com.example.eshop.util;

import com.example.eshop.entity.User;
import jakarta.servlet.http.HttpSession;

public class AuthUtil {
    /**
     * 检查用户是否已登录
     * @param session HttpSession对象
     * @return 已登录的用户对象，未登录返回null
     */
    public static User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    /**
     * 检查用户是否已登录
     * @param session HttpSession对象
     * @return 是否已登录
     */
    public static boolean isLoggedIn(HttpSession session) {
        return getCurrentUser(session) != null;
    }

    /**
     * 检查用户是否为管理员
     * @param session HttpSession对象
     * @return 是否为管理员
     */
    public static boolean isAdmin(HttpSession session) {
        User user = getCurrentUser(session);
        return user != null && "ADMIN".equals(user.getRole());
    }
}