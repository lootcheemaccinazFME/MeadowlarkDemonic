@ECHO OFF
SET DIR=%~dp0
SET CLASSPATH=%DIR%gradle\wrapper\gradle-wrapper.jar
IF NOT EXIST "%CLASSPATH%" (
  ECHO gradle-wrapper.jar is missing. Run: gradle wrapper --gradle-version 8.10.2 1>&2
  EXIT /B 1
)
java -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
