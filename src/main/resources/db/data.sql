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
('纯棉T恤', '100%纯棉，舒适透气', 99.00, 500, 2, '/images/tshirt.jpg', 'ON_SALE'),
('运动鞋', '轻便透气，适合跑步', 299.00, 300, 2, '/images/shoes.jpg', 'ON_SALE'),
('牛仔裤', '经典版型，修身显瘦', 199.00, 400, 2, '/images/jeans.jpg', 'ON_SALE'),
('羽绒服', '90%白鸭绒，保暖舒适', 599.00, 150, 2, '/images/jacket.jpg', 'ON_SALE'),
('四件套', '纯棉斜纹，亲肤舒适', 299.00, 200, 3, '/images/bedding.jpg', 'ON_SALE'),
('保温杯', '304不锈钢，24小时保温', 89.00, 500, 3, '/images/cup.jpg', 'ON_SALE'),
('台灯', 'LED护眼，三档调光', 129.00, 300, 3, '/images/lamp.jpg', 'ON_SALE'),
('收纳箱', '大容量，透明可视', 49.00, 600, 3, '/images/storage.jpg', 'ON_SALE'),
('Java编程思想', '经典Java编程书籍', 108.00, 100, 4, '/images/java.jpg', 'ON_SALE'),
('原子习惯', '詹姆斯·克利尔著', 45.00, 200, 4, '/images/habits.jpg', 'ON_SALE'),
('笔记本套装', 'A5规格，5本装', 25.00, 1000, 4, '/images/notebook.jpg', 'ON_SALE'),
('中性笔', '0.5mm，书写流畅', 2.50, 2000, 4, '/images/pen.jpg', 'ON_SALE'),
('巧克力礼盒', '比利时进口，精美包装', 168.00, 100, 5, '/images/chocolate.jpg', 'ON_SALE'),
('坚果礼盒', '混合坚果，健康美味', 128.00, 150, 5, '/images/nuts.jpg', 'ON_SALE'),
('纯牛奶', '全脂纯牛奶，1L装', 12.00, 500, 5, '/images/milk.jpg', 'ON_SALE'),
('咖啡豆', '阿拉比卡豆，中度烘焙', 68.00, 200, 5, '/images/coffee.jpg', 'ON_SALE');