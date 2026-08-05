<#if projectType == 'SMART' || projectType == 'SMART_ORM'>
${remoteConfigPackage}.${moduleName?cap_first}ApiConfig
<#else >
${apiConfigPackage}.${moduleName?cap_first}ApiConfig
</#if>
