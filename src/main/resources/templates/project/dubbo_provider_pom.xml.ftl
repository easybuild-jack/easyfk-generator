<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>${groupId }</groupId>
        <artifactId>${artifactId!projectName }-remote</artifactId>
        <version>${projectVersion }</version>
    </parent>
    <artifactId>${artifactId!projectName }-dubbo-provider</artifactId>
    <packaging>jar</packaging>

    <name>${artifactId!projectName }-dubbo-provider</name>
    <!-- FIXME change it to the project's website -->
    <url>http://www.example.com</url>

    <dependencies>
        <dependency>
            <groupId>${groupId }</groupId>
            <artifactId>${artifactId!projectName }-server</artifactId>
            <version><#noparse>${project.parent.version}</#noparse></version>
        </dependency>
        <dependency>
            <groupId>${groupId }</groupId>
            <artifactId>${artifactId!projectName }-dubbo-api</artifactId>
            <version><#noparse>${project.parent.version}</#noparse></version>
        </dependency>
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>web-micro</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>rpc-dubbo</artifactId>
            <scope>provided</scope>
        </dependency>
<#if modules?has_content>
<#list modules as module>
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>${module.name}-dubbo-provider</artifactId>
        </dependency>
</#list>
</#if>
    </dependencies>
</project>