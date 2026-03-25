document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM加载完成，初始化搜索建议功能');

    const searchInput = document.getElementById('search-input');
    const suggestionsContainer = document.getElementById('search-suggestions');

    console.log('搜索输入元素:', searchInput);
    console.log('建议容器元素:', suggestionsContainer);

    let timeoutId;

    // 确保元素存在
    if (!searchInput || !suggestionsContainer) {
        console.error('搜索元素未找到');
        return;
    }

    // 处理搜索输入
    searchInput.addEventListener('input', function() {
        const keyword = this.value.trim();
        console.log('输入内容:', keyword);

        // 清除之前的定时器
        clearTimeout(timeoutId);

        if (keyword.length > 0) {
            // 延迟500ms发送请求，避免频繁请求
            timeoutId = setTimeout(() => {
                console.log('发送搜索建议请求:', keyword);
                fetch('/api/search-suggestions?keyword=' + encodeURIComponent(keyword))
                    .then(response => {
                        console.log('响应状态:', response.status);
                        return response.json();
                    })
                    .then(data => {
                        console.log('搜索建议数据:', data);
                        displaySuggestions(data);
                    })
                    .catch(error => console.error('搜索建议加载失败:', error));
            }, 500);
        } else {
            // 清空输入时隐藏建议
            hideSuggestions();
        }
    });

    // 显示建议
    function displaySuggestions(suggestions) {
        console.log('显示搜索建议:', suggestions);
        if (!Array.isArray(suggestions) || suggestions.length === 0) {
            console.log('无搜索建议数据');
            hideSuggestions();
            return;
        }

        suggestionsContainer.innerHTML = '';

        suggestions.forEach(suggestion => {
            const div = document.createElement('div');
            div.textContent = suggestion;
            div.addEventListener('click', function() {
                // 设置搜索框的值并提交表单
                searchInput.value = suggestion;
                searchInput.closest('form').submit();
            });
            suggestionsContainer.appendChild(div);
        });

        console.log('显示建议容器');
        suggestionsContainer.style.display = 'block';
        console.log('建议容器显示状态:', suggestionsContainer.style.display);
    }

    // 隐藏建议
    function hideSuggestions() {
        console.log('隐藏建议容器');
        suggestionsContainer.style.display = 'none';
    }

    // 点击页面其他地方时隐藏建议
    document.addEventListener('click', function(event) {
        if (!event.target.closest('.search-container')) {
            hideSuggestions();
        }
    });
});