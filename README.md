##GitHub OAuth接入
https://developer.github.com/apps/building-oauth-apps/creating-an-oauth-app/

##flywaydb插件
```xml
 <plugin>
                <groupId>org.flywaydb</groupId>
                <artifactId>flyway-maven-plugin</artifactId>
                <version>5.2.4</version>
                <configuration>
                    <url>jdbc:mysql://192.168.2.211:3306/community</url>
                    <user>root</user>
                    <password>123456</password>
                </configuration>
            </plugin>
```
```xml
    <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
            <version>5.2.4</version>
    </dependency>
```
##sql-v1
```sql
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `token` char(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `gmt_create` bigint(255) NULL DEFAULT NULL,
  `gmt_modified` bigint(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
```
##sql-v2
```sql
ALTER TABLE `community`.`user`
ADD COLUMN `bio` varchar(255) NULL AFTER `gmt_modified`;
```

#mybatis Generator 
```text
mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate
```

#mybatis plus
主键id注解要为
```text
  @TableId(type = IdType.AUTO)
```

```text
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-generator</artifactId>
            <version>3.2.0</version>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.2.0</version>
        </dependency>

```






