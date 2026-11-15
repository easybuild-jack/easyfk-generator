subprojects {
    dependencies {
        add("api", project(":${artifactId!projectName }-api"))
    }
}
