<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>${groupId }</groupId>
    <artifactId>${artifactId!projectName }</artifactId>
    <packaging>pom</packaging>
    <version>${projectVersion }</version>

    <modules>
        <module>${artifactId!projectName }-server</module>
        <module>${artifactId!projectName }-api</module>
        <#if projectType =="SMART" || projectType =="SMART_ORM" >
        <module>${artifactId!projectName }-remote</module>
        </#if>
        <#if projectType =="SMART_ORM" >
        <module>${artifactId!projectName }-orm</module>
        </#if>
        <#if prdType =="SINGLE" >
        <module>${artifactId!projectName }-prd</module>
        </#if>
        <#if prdType =="SEPARATION" >
        <module>${artifactId!projectName }-prd-client</module>
        <module>${artifactId!projectName }-prd-bms</module>
        </#if>
    </modules>
    <name>${artifactId!projectName }</name>
    <!-- FIXME change it to the project's website -->
    <url>http://www.example.com</url>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <framework.version>${frameworkVersion }</framework.version>
    </properties>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.mcst</groupId>
                <artifactId>easyfk-dependencies</artifactId>
                <version><#noparse>${framework.version}</#noparse></version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>com.mcst</groupId>
                <artifactId>module-dependencies</artifactId>
                <version><#noparse>${framework.version}</#noparse></version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <scope>compile</scope>
        </dependency>
    </dependencies>
    <build>
      <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.14.1</version>
            <configuration>
                <source><#noparse>${maven.compiler.source}</#noparse></source>
                <target>21</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.46</version>
                    </path>
                    <path>
                        <groupId>io.github.linpeilie</groupId>
                        <artifactId>mapstruct-plus-processor</artifactId>
                        <version>1.5.3</version>
                    </path>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok-mapstruct-binding</artifactId>
                        <version>0.2.0</version>
                    </path>
<#if ormType??><#if ormType.name() == "MYBATIS_FLEX">
                    <path>
                        <groupId>com.mybatis-flex</groupId>
                        <artifactId>mybatis-flex-processor</artifactId>
                        <version>1.11.8</version>
                    </path>
</#if></#if>
                </annotationProcessorPaths>
                <compilerArgs>
                    <arg>-Amapstruct.plus.nullValuePropertyMappingStrategy=IGNORE</arg>
                </compilerArgs>
            </configuration>
        </plugin>
      </plugins>
    </build>
</project>
