@echo off
setlocal
set "MAVEN_PROJECTBASEDIR=%~dp0"
set "MAVEN_WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
if not exist "%MAVEN_WRAPPER_JAR%" (
  echo Maven wrapper jar not found: %MAVEN_WRAPPER_JAR%
  exit /b 1
)
java -Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR% -jar "%MAVEN_WRAPPER_JAR%" %*
