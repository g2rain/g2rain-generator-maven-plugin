# 排障

| 现象 | 优先检查 |
| --- | --- |
| Goal 无法解析 | 插件坐标、版本、仓库和 descriptor |
| JDBC 失败 | URL、驱动、最小权限账号、网络和数据库版本 |
| 未生成表 | tables、大小写、schema 和元数据权限 |
| 文件未更新 | overwrite 与 skipIfExists |
| 路径错误 | projectName、basePackage、stepIn 和目标根 |
| 代码不能编译 | 模板、列类型、Generator/Crafter/Profile 兼容 |

日志不得输出数据库密码。
