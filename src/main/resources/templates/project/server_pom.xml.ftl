<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>${groupId }</groupId>
        <artifactId>${artifactId!projectName }</artifactId>
        <version>${projectVersion }</version>
    </parent>
    <artifactId>${artifactId!projectName }-server</artifactId>

    <name>${artifactId!projectName }-server</name>
    <!-- FIXME change it to the project's website -->
    <url>http://www.example.com</url>

    <dependencies>
        <dependency>
            <groupId>${groupId }</groupId>
            <artifactId>${artifactId!projectName }-api</artifactId>
            <version><#noparse>${project.parent.version}</#noparse></version>
        </dependency>
    <#if projectType == "SINGLE">
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>web-prd</artifactId>
        </dependency>
    <#elseif projectType == "MICROSERVICE">
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>web-micro</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>registry-nacos</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>rpc-${rpcType?lower_case}</artifactId>
        </dependency>
    </#if >
<#if projectType == "SMART_ORM">
        <dependency>
            <groupId>${groupId }</groupId>
            <artifactId>${artifactId!projectName }-orm-api</artifactId>
            <version><#noparse>${project.parent.version}</#noparse></version>
        </dependency>
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>service-base</artifactId>
        </dependency>
<#elseif ormType?? && ormType != "NONE">
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>orm-${ormModuleSuffix}</artifactId>
        </dependency>
<#if dbType?? && dbType.name() == "POSTGRE_SQL">
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>db-postgresql</artifactId>
        </dependency>
<#else>
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>db-mysql</artifactId>
        </dependency>
</#if>
</#if>
    <#if projectType != "SMART" && projectType != "SMART_ORM" >
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>log-${logType?lower_case}</artifactId>
        </dependency>
    </#if>
<#if includeAuth>
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>auth-server</artifactId>
        </dependency>
<#if ormType?? && ormType != "NONE">
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>auth-orm-${ormBusinessSuffix}</artifactId>
        </dependency>
</#if>
</#if>
    </dependencies>
    <#if projectType != "SMART" && projectType != "SMART_ORM" >
    <build>
        <finalName>${projectName}-server</finalName>
        <plugins>
            <!-- 依赖分离打包方式 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <classpathPrefix>lib/</classpathPrefix>
                            <mainClass>${basePackage}.${moduleName?cap_first}ServerApp</mainClass>
                            <useUniqueVersions>false</useUniqueVersions>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>

            <!-- 复制依赖到lib目录 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>3.2.0</version>
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
                <version>3.0.0-M7</version>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
        </plugins>
    </build>
    </#if>
</project>
