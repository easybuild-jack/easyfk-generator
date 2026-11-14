<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>${groupId }</groupId>
        <artifactId>${artifactId!projectName }</artifactId>
        <version>${projectVersion }</version>
    </parent>
    <artifactId>${artifactId!projectName }-prd-client</artifactId>
    <packaging>jar</packaging>

    <name>${artifactId!projectName }-prd-client</name>
    <!-- FIXME change it to the project's website -->
    <url>http://www.example.com</url>

    <dependencies>
        <dependency>
            <groupId>${groupId }</groupId>
            <artifactId>${artifactId!projectName }-api</artifactId>
            <version><#noparse>${project.parent.version}</#noparse></version>
        </dependency>
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>web-prd</artifactId>
        </dependency>
        <#if projectType == "MICROSERVICE" || projectType == "MICRO_PRD">
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>doc-knife4j</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>registry-nacos</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>rpc-${rpcType?lower_case}</artifactId>
        </dependency>
        </#if>
        <#if projectType != "SMART" && projectType != "SMART_ORM">
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>log-${logType?lower_case}</artifactId>
        </dependency>
        </#if>
    </dependencies>

    <#if projectType == "MICROSERVICE">
    <build>
        <finalName>${projectName}-prd-client</finalName>
        <plugins>
            <!-- 依赖分离打包方式 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.4.2</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <classpathPrefix>lib/</classpathPrefix>
                            <mainClass>${basePackage}.${moduleName?cap_first}PrdClientApp</mainClass>
                            <useUniqueVersions>false</useUniqueVersions>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>

            <!-- 复制依赖到lib目录 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>3.8.1</version>
                <executions>
                    <execution>
                        <id>copy-dependencies</id>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <outputDirectory><#noparse>${project.build.directory}/lib</#noparse></outputDirectory>
                            <useRepositoryLayout>false</useRepositoryLayout>
                            <stripVersion>false</stripVersion>
                            <stripClassifier>false</stripClassifier>
                            <!-- 移除includeScope限制，确保所有依赖都被复制和引用 -->
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- 跳过测试 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.6</version>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
        </plugins>
    </build>
    </#if>
</project>
