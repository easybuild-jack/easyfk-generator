<#if projectType == 'SMART'>
${remoteConfigPackage}.${moduleName?cap_first}ApiConfig
<#else >
${apiConfigPackage}.${moduleName?cap_first}ApiConfig
</#if>
