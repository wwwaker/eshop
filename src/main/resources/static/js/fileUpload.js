document.addEventListener('DOMContentLoaded', function() {
    const fileInput = document.getElementById('fileInput');
    if (fileInput) {
        fileInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const formData = new FormData();
                formData.append('file', file);

                fetch('/admin/upload', {
                    method: 'POST',
                    body: formData
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.url) {
                            document.getElementById('imageUrl').value = data.url;
                            const previewImage = document.getElementById('previewImage');
                            const noImage = document.getElementById('noImage');
                            if (previewImage) {
                                previewImage.src = data.url;
                                previewImage.style.display = 'block';
                            }
                            if (noImage) {
                                noImage.style.display = 'none';
                            }
                        } else {
                            alert('上传失败: ' + data.error);
                        }
                    })
                    .catch(error => {
                        alert('上传失败: ' + error.message);
                    });
            }
        });
    }
});