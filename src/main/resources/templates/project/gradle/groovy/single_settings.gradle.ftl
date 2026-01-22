rootProject.name = '${artifactId!projectName }'
dependencyResolutionManagement.versionCatalogs{
    libs {
        library("dependencies", "com.mcst:easyfk-dependencies:3.2.12")
        library('lombok', 'org.projectlombok:lombok:1.18.38')
        library('lombokMapstruct', 'org.projectlombok:lombok-mapstruct-binding:0.2.0')
        library('mapstructProcessor', 'io.github.linpeilie:mapstruct-plus-processor:1.5.0')
        library('processor', 'org.springframework.boot:spring-boot-configuration-processor:3.2.12')
    }
}
