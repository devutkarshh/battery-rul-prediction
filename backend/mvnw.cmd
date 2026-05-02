@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script for Windows
@REM ----------------------------------------------------------------------------

@REM Begin all REM://
@echo off
@REM set title
title %0

set WRAPPER_JAR="%~dp0\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
set MAVEN_PROJECTBASEDIR=%~dp0

@REM Check if JAVA_HOME is set
if not "%JAVA_HOME%"=="" goto OkJHome
for %%i in (java.exe) do set "JAVACMD=%%~$PATH:i"
goto checkJCmd

:OkJHome
set "JAVACMD=%JAVA_HOME%\bin\java.exe"

:checkJCmd
if exist "%JAVACMD%" goto chkMWrapperJar

echo Error: JAVA_HOME is not defined correctly or java is not in PATH. >&2
goto error

:chkMWrapperJar
if exist %WRAPPER_JAR% goto runMaven

@REM Download maven-wrapper.jar
echo Downloading Maven Wrapper...
powershell -Command "(New-Object Net.WebClient).DownloadFile('%WRAPPER_URL:"=%', '%WRAPPER_JAR:"=%')"
if ERRORLEVEL 1 goto error

:runMaven
set MAVEN_CMD_LINE_ARGS=%*
"%JAVACMD%" ^
  -jar %WRAPPER_JAR% %MAVEN_CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
exit /B %ERROR_CODE%
