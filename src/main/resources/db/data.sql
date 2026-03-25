-- 插入商品分类
INSERT INTO categories (name, description, sort_order) VALUES
('电子产品', '手机、电脑、平板等数码产品', 1),
('服装鞋帽', '男装、女装、童装、鞋靴', 2),
('家居用品', '家具、家纺、厨具', 3),
('图书文具', '图书、办公用品、文具', 4),
('食品饮料', '零食、饮料、生鲜', 5);

-- 插入商品数据
INSERT INTO products (name, description, price, stock, category_id, image_url, status) VALUES
('iPhone 17 Pro', '苹果最新旗舰手机，A19 Pro芯片', 8999.00, 100, 1, '/images/iphone17.png', 'ON_SALE'),
('MacBook Pro M5', 'M5 Pro芯片，14英寸笔记本电脑', 13999.00, 50, 1, '/images/macbook.png', 'ON_SALE'),
('iPad Air', 'M4芯片，10.9英寸平板电脑', 4799.00, 80, 1, '/images/ipad.jpg', 'ON_SALE'),
('AirPods Pro 3', '主动降噪无线耳机', 1899.00, 200, 1, '/images/airpods.png', 'ON_SALE'),
('笔记本套装', 'A5规格，5本装', 25.00, 1000, 2, '/images/notebook.jpg', 'ON_SALE'),
('中性笔', '0.5mm，书写流畅', 2.50, 2000, 2, '/images/pen.jpg', 'ON_SALE'),
('巧克力礼盒', '比利时进口，精美包装', 168.00, 100, 3, '/images/chocolate.jpg', 'ON_SALE'),
('坚果礼盒', '混合坚果，健康美味', 128.00, 150, 3, '/images/nuts.jpg', 'ON_SALE'),
('纯牛奶', '全脂纯牛奶，1L装', 12.00, 500, 4, '/images/milk.jpg', 'ON_SALE'),
('咖啡豆', '阿拉比卡豆，中度烘焙', 68.00, 200, 5, '/images/coffee.jpg', 'ON_SALE');