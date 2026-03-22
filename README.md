# EShop



## 快速开始

### 1. 创建数据库

执行 `src/main/resources/db/schema.sql` 文件创建数据库和表结构：

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

或手动在MySQL中执行

### 2. 初始化数据（可选）

执行 `src/main/resources/db/data.sql` 文件初始化测试商品数据：

```bash
mysql -u root -p eshop < src/main/resources/db/data.sql
```

### 3. 配置应用

复制配置文件模板并修改数据库连接信息：

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

修改 `application.properties`：
```properties
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码
```

### 4. 运行项目

```bash
mvn spring-boot:run
```

### 5. 访问应用

- 前台：http://localhost:8080
- 后台：http://localhost:8080/admin

## 默认账号

应用启动后会自动创建以下默认账号：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| user | user123 | 普通用户 |

## 注意事项

- 确保MySQL服务已启动
- 数据库配置信息不要提交到版本库
- 使用 `application.properties.example` 作为配置模板
