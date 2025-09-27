<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>${groupId }</groupId>
        <artifactId>${artifactId!projectName }</artifactId>
        <version>${projectVersion }</version>
    </parent>
    <artifactId>${artifactId!projectName }-api</artifactId>
    <packaging>jar</packaging>

    <name>${artifactId!projectName }-api</name>
    <!-- FIXME change it to the project's website -->
    <url>http://www.example.com</url>

    <dependencies>
        <#if rpcType == "CLOUD">
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
            <scope>compile</scope>
        </dependency>
        </#if>
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>remote-${rpcType?lower_case}</artifactId>
        </dependency>
    </dependencies>
</project>
