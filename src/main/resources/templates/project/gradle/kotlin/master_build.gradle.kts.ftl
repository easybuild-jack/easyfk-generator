// 版本目录（version catalog）在 root 脚本的 subprojects 块内无法通过 libs 访问器读取，统一通过该函数取用
val libsCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun catalogLibrary(alias: String) = libsCatalog.findLibrary(alias).get()

// 所有项目通用配置
allprojects {
    // 指定项目版本号和 group
    version = "${projectVersion }"
    group = "${groupId }"

    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        withSourcesJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}
subprojects {
    dependencies {
        add("implementation", platform(catalogLibrary("dependencies")))
        add("implementation", platform(catalogLibrary("moduleDependencies")))
        add("compileOnly", catalogLibrary("lombok"))
        add("testCompileOnly", catalogLibrary("lombok"))
        add("annotationProcessor", catalogLibrary("lombok"))
        add("annotationProcessor", catalogLibrary("processor"))
        add("annotationProcessor", catalogLibrary("lombokMapstruct"))
        add("annotationProcessor", catalogLibrary("mapstructProcessor"))
        add("testAnnotationProcessor", catalogLibrary("lombok"))
        add("testAnnotationProcessor", catalogLibrary("processor"))
        add("testAnnotationProcessor", catalogLibrary("lombokMapstruct"))
        add("testAnnotationProcessor", catalogLibrary("mapstructProcessor"))
<#if ormType??><#if ormType.name() == "MYBATIS_FLEX">
        add("annotationProcessor", catalogLibrary("flexProcessor"))
</#if></#if>
    }
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-Amapstruct.plus.nullValuePropertyMappingStrategy=IGNORE")
    }
}
