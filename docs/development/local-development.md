# 本地开发

需要 JDK 25、Maven 3.9。基础命令为 `mvn test`、`mvn clean package` 和 `mvn clean install`。

真实生成只使用临时目录和隔离数据库，只授予元数据读取权限，不在业务仓库直接验证覆盖或异常路径。
