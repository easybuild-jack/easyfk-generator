dependencies {
    api ('com.mcst:web-micro')
    api project(':${artifactId!projectName }-remote:${artifactId!projectName }-dubbo-api')
    api project(':${artifactId!projectName }-server')
    compileOnly('com.mcst:rpc-dubbo')
}