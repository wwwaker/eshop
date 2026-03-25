let usernameValid = false;
let emailValid = false;
let phoneValid = false;

// 检查用户名是否已存在
function checkUsername() {
    const username = document.getElementById('username').value.trim();
    const hint = document.getElementById('usernameHint');

    if (username === '') {
        hint.innerHTML = '';
        usernameValid = false;
        return;
    }

    // 简单的用户名格式验证
    if (username.length < 3 || username.length > 20) {
        hint.innerHTML = '<span style="color: orange;">用户名长度应在3-20个字符之间</span>';
        usernameValid = false;
        return;
    }

    // 发送AJAX请求检查用户名
    fetch(`/api/user/check-username?username=${encodeURIComponent(username)}`)
        .then(response => response.json())
        .then(data => {
            if (data.exists) {
                hint.innerHTML = '<span style="color: red;">❌ ' + data.message + '</span>';
                usernameValid = false;
            } else {
                hint.innerHTML = '<span style="color: green;">✅ ' + data.message + '</span>';
                usernameValid = true;
            }
        })
        .catch(error => {
            console.error('检查用户名时出错:', error);
            hint.innerHTML = '<span style="color: orange;">检查用户名时出错，请稍后重试</span>';
            usernameValid = false;
        });
}

// 检查邮箱是否已存在
function checkEmail() {
    const email = document.getElementById('email').value.trim();
    const hint = document.getElementById('emailHint');

    if (email === '') {
        hint.innerHTML = '';
        emailValid = false;
        return;
    }

    // 简单的邮箱格式验证
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        hint.innerHTML = '<span style="color: orange;">请输入有效的邮箱地址</span>';
        emailValid = false;
        return;
    }

    // 发送AJAX请求检查邮箱
    fetch(`/api/user/check-email?email=${encodeURIComponent(email)}`)
        .then(response => response.json())
        .then(data => {
            if (data.exists) {
                hint.innerHTML = '<span style="color: red;">❌ ' + data.message + '</span>';
                emailValid = false;
            } else {
                hint.innerHTML = '<span style="color: green;">✅ ' + data.message + '</span>';
                emailValid = true;
            }
        })
        .catch(error => {
            console.error('检查邮箱时出错:', error);
            hint.innerHTML = '<span style="color: orange;">检查邮箱时出错，请稍后重试</span>';
            emailValid = false;
        });
}

// 检查手机号格式
function checkPhone() {
    const phone = document.getElementById('phone').value.trim();
    const hint = document.getElementById('phoneHint');

    if (phone === '') {
        hint.innerHTML = '<span style="color: red;">❌ 手机号不能为空</span>';
        phoneValid = false;
        return;
    }

    // 手机号格式验证（11位数字，1开头）
    const phoneRegex = /^1[3-9]\d{9}$/;
    if (!phoneRegex.test(phone)) {
        hint.innerHTML = '<span style="color: orange;">请输入有效的11位手机号</span>';
        phoneValid = false;
        return;
    }

    hint.innerHTML = '<span style="color: green;">✅ 手机号格式正确</span>';
    phoneValid = true;
}

// 表单提交验证
function validateForm() {
    const username = document.getElementById('username').value.trim();
    const email = document.getElementById('email').value.trim();
    const phone = document.getElementById('phone').value.trim();

    // 检查必填字段是否为空
    if (email === '') {
        alert('邮箱不能为空');
        document.getElementById('email').focus();
        return false;
    }

    if (phone === '') {
        alert('手机号不能为空');
        document.getElementById('phone').focus();
        return false;
    }

    if (!usernameValid) {
        alert('请检查用户名是否正确，用户名不能重复');
        document.getElementById('username').focus();
        return false;
    }

    if (!emailValid) {
        alert('请检查邮箱是否正确，邮箱不能重复');
        document.getElementById('email').focus();
        return false;
    }

    if (!phoneValid) {
        alert('请检查手机号格式是否正确');
        document.getElementById('phone').focus();
        return false;
    }

    return true;
}

// 添加输入事件监听器，实现实时检查
document.addEventListener('DOMContentLoaded', function() {
    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const phoneInput = document.getElementById('phone');

    // 防抖函数，避免频繁请求
    function debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // 添加防抖的实时检查
    usernameInput.addEventListener('input', debounce(checkUsername, 500));
    emailInput.addEventListener('input', debounce(checkEmail, 500));
    phoneInput.addEventListener('input', debounce(checkPhone, 500));
});