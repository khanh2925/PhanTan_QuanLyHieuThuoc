@REM Maven Wrapper script for Windows
@REM This script downloads Maven if needed and runs it
@echo off
setlocal

set JAVA_HOME=C:\Users\khanh\.jdks\ms-17.0.18
set PATH=%JAVA_HOME%\bin;%PATH%

set MAVEN_PROJECTBASEDIR=%~dp0
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"

if exist %WRAPPER_JAR% (
    java -jar %WRAPPER_JAR% %*
) else (
    echo Maven wrapper JAR not found, using direct download...
    set MVN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6
    if not exist "%MVN_HOME%\bin\mvn.cmd" (
        echo Downloading Maven 3.9.6...
        powershell -Command "& { $url='https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip'; $out='%TEMP%\maven.zip'; Invoke-WebRequest -Uri $url -OutFile $out; Expand-Archive -Path $out -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force; Remove-Item $out }"
    )
    "%MVN_HOME%\bin\mvn.cmd" %*
)
