<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>${groupId }</groupId>
        <artifactId>${artifactId!projectName }</artifactId>
        <version>${projectVersion }</version>
    </parent>
    <artifactId>${artifactId!projectName }-prd</artifactId>
    <packaging>pom</packaging>

    <modules>
        <module>${artifactId!projectName }-prd-client</module>
        <module>${artifactId!projectName }-prd-bms</module>
    </modules>
    <name>${artifactId!projectName }-prd</name>
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
        <#if projectType == "MICROSERVICE">
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>registry-nacos</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>rpc-${rpcType?lower_case}</artifactId>
        </dependency>
        </#if>
        <dependency>
            <groupId>com.mcst</groupId>
            <artifactId>log-${logType?lower_case}</artifactId>
        </dependency>
    </dependencies>

</project>
