dependencies {
    api ('com.mcst:web-micro')
    api project(':${artifactId!projectName }-remote:${artifactId!projectName }-cloud-api')
    api project(':${artifactId!projectName }-server')
    compileOnly ('com.mcst:remote-cloud')
}
