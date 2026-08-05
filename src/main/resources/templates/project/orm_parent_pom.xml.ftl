<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>${groupId }</groupId>
        <artifactId>${artifactId!projectName }</artifactId>
        <version>${projectVersion }</version>
    </parent>
    <artifactId>${artifactId!projectName }-orm</artifactId>
    <packaging>pom</packaging>

    <modules>
        <module>${artifactId!projectName }-orm-api</module>
        <#list ormModuleList as om>
        <module>${artifactId!projectName }-orm-${om}</module>
        </#list>
    </modules>
    <name>${artifactId!projectName }-orm</name>
    <!-- FIXME change it to the project's website -->
    <url>http://www.example.com</url>
    <dependencies>
        <dependency>
            <groupId>${groupId }</groupId>
            <artifactId>${artifactId!projectName }-api</artifactId>
            <version><#noparse>${project.parent.version}</#noparse></version>
        </dependency>
    </dependencies>
</project>
