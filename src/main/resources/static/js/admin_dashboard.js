document.addEventListener('DOMContentLoaded', function() {
    var salesTrendData = window.salesTrendData || [];

    setTimeout(function() {
        var chartLoading = document.getElementById('chartLoading');
        var chartNoData = document.getElementById('chartNoData');
        var canvas = document.getElementById('salesTrendChart');

        if (!chartLoading || !chartNoData || !canvas) {
            console.error('图表元素未找到');
            return;
        }

        if (!salesTrendData || salesTrendData.length === 0) {
            chartLoading.style.display = 'none';
            chartNoData.style.display = 'block';
            canvas.style.display = 'none';
            return;
        }

        chartLoading.style.display = 'none';
        canvas.style.display = 'block';

        var ctx = canvas.getContext('2d');

        var labels = salesTrendData.map(function(item) {
            var date = new Date(item.date);
            return (date.getMonth() + 1) + '/' + date.getDate();
        });

        var orderCounts = salesTrendData.map(function(item) {
            return item.orderCount || 0;
        });

        var amounts = salesTrendData.map(function(item) {
            return parseFloat(item.totalAmount) || 0;
        });

        var chartInstance = null;

        try {
            chartInstance = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: '订单数量',
                            data: orderCounts,
                            borderColor: 'rgb(75, 192, 192)',
                            backgroundColor: 'rgba(75, 192, 192, 0.2)',
                            tension: 0.4,
                            yAxisID: 'y'
                        },
                        {
                            label: '销售金额',
                            data: amounts,
                            borderColor: 'rgb(255, 99, 132)',
                            backgroundColor: 'rgba(255, 99, 132, 0.2)',
                            tension: 0.4,
                            yAxisID: 'y1'
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    animation: {
                        duration: 800
                    },
                    interaction: {
                        mode: 'index',
                        intersect: false
                    },
                    scales: {
                        y: {
                            type: 'linear',
                            display: true,
                            position: 'left',
                            title: {
                                display: true,
                                text: '订单数'
                            }
                        },
                        y1: {
                            type: 'linear',
                            display: true,
                            position: 'right',
                            title: {
                                display: true,
                                text: '金额 (¥)'
                            },
                            grid: {
                                drawOnChartArea: false
                            }
                        }
                    },
                    plugins: {
                        legend: {
                            display: true,
                            position: 'top'
                        },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    var label = context.dataset.label || '';
                                    if (label) {
                                        label += ': ';
                                    }
                                    if (context.datasetIndex === 1) {
                                        label += '¥' + context.parsed.y.toFixed(2);
                                    } else {
                                        label += context.parsed.y;
                                    }
                                    return label;
                                }
                            }
                        }
                    }
                }
            });
        } catch (e) {
            console.error('图表加载失败:', e);
            if (chartLoading) {
                chartLoading.style.display = 'block';
                chartLoading.textContent = '图表加载失败，请刷新页面重试';
            }
        }
    }, 300);
});