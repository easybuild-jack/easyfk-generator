plugins {
    `java-library`
}

dependencies {
    api(project(":${artifactId!projectName }-orm:${artifactId!projectName }-orm-api"))
    implementation("com.mcst:orm-hibernate")
}
