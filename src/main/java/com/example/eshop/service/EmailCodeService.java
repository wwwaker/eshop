package com.example.eshop.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EmailCodeService {

    private static final String EMAIL_CODE_KEY = "emailCode";
    private static final String EMAIL_CODE_ADDRESS_KEY = "emailCodeAddress";
    private static final String EMAIL_CODE_EXPIRE_AT_KEY = "emailCodeExpireAt";
    private static final int EXPIRE_MINUTES = 5;

    public String generateCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(code);
    }

    public void saveCode(HttpSession session, String email, String code) {
        session.setAttribute(EMAIL_CODE_KEY, code);
        session.setAttribute(EMAIL_CODE_ADDRESS_KEY, email);
        session.setAttribute(EMAIL_CODE_EXPIRE_AT_KEY, LocalDateTime.now().plusMinutes(EXPIRE_MINUTES));
    }

    public boolean verifyCode(HttpSession session, String email, String code) {
        Object savedCode = session.getAttribute(EMAIL_CODE_KEY);
        Object savedEmail = session.getAttribute(EMAIL_CODE_ADDRESS_KEY);
        Object expireAt = session.getAttribute(EMAIL_CODE_EXPIRE_AT_KEY);

        if (!(savedCode instanceof String) || !(savedEmail instanceof String) || !(expireAt instanceof LocalDateTime)) {
            return false;
        }

        if (LocalDateTime.now().isAfter((LocalDateTime) expireAt)) {
            clearCode(session);
            return false;
        }

        return savedEmail.equals(email) && savedCode.equals(code);
    }

    public void clearCode(HttpSession session) {
        session.removeAttribute(EMAIL_CODE_KEY);
        session.removeAttribute(EMAIL_CODE_ADDRESS_KEY);
        session.removeAttribute(EMAIL_CODE_EXPIRE_AT_KEY);
    }

    public int getExpireMinutes() {
        return EXPIRE_MINUTES;
    }
}
