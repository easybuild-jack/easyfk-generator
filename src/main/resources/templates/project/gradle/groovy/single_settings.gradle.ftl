rootProject.name = '${artifactId!projectName }'
dependencyResolutionManagement.versionCatalogs{
    libs {
        library("dependencies", "com.mcst:easyfk-dependencies:${frameworkVersion }")
        library('lombok', 'org.projectlombok:lombok:1.18.38')
        library('processor', 'org.springframework.boot:spring-boot-configuration-processor:3.2.12')
    }
}
