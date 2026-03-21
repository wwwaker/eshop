# EShop



## 快速开始

### 1. 创建数据库

```sql
CREATE DATABASE eshop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 配置应用

复制配置文件模板并修改数据库连接信息：

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

修改 `application.properties`：
```properties
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码
```

### 3. 运行项目

```bash
mvn spring-boot:run
```

### 4. 访问应用

- 前台：http://localhost:8080
- 后台：http://localhost:8080/admin

## 注意事项

- 确保MySQL服务已启动
- 数据库配置信息不要提交到版本库
- 使用 `application.properties.example` 作为配置模板
