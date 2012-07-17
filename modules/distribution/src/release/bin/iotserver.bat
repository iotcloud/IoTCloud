@echo off
rem ---------------------------------------------------------------------------
rem Startup script for the ciphertool
rem
rem Environment Variable Prerequisites
rem
rem   SCG_HOME      Must point at your SCG directory
rem
rem   JAVA_HOME       Must point at your Java Development Kit installation.
rem
rem   JAVA_OPTS       (Optional) Java runtime options
rem ---------------------------------------------------------------------------
set CURRENT_DIR=%cd%
rem set _XDEBUG="-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000"

rem Make sure prerequisite environment variables are set
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo The JAVA_HOME environment variable is not defined
echo This environment variable is needed to run this program
goto end
:gotJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
goto okJavaHome
:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
echo NB: JAVA_HOME should point to a JDK/JRE
goto end
:okJavaHome

rem check the SCG_HOME environment variable
if not "%SCG_HOME%" == "" goto gotHome
set SCG_HOME=%CURRENT_DIR%/..

if exist "%SCG_HOME%\bin\iotserver.bat" goto okHome

rem guess the home. Jump one directory up to check if that is the home
cd ..
set SCG_HOME=%cd%/..
cd %SCG_HOME%

echo %SCG_HOME%

:gotHome
if exist "%SCG_HOME%\bin\iotserver.bat" goto okHome

rem set SCG_HOME=%~dp0..
if exist "%SCG_HOME%\bin\iotserver.bat" goto okHome

echo The SCG_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end

:okHome
rem set the classes
setlocal EnableDelayedExpansion
rem loop through the libs and add them to the class path
cd "%SCG_HOME%"
set SCG_CLASSPATH=.\conf
FOR %%C in ("%SCG_HOME%\lib\*.jar") DO set SCG_CLASSPATH=!SCG_CLASSPATH!;".\lib\%%~nC%%~xC"
rem FOR %%C in ("%SCG_HOME%\lib\commons-*.jar") DO set SCG_CLASSPATH=!SCG_CLASSPATH!;".\lib\%%~nC%%~xC"



rem ----- Execute The Requested Command ---------------------------------------
echo Using SCG_HOME:   %SCG_HOME%
echo Using JAVA_HOME:    %JAVA_HOME%
set _RUNJAVA="%JAVA_HOME%\bin\java"

set JAVA_ENDORSED=".\lib\endorsed";"%JAVA_HOME%\jre\lib\endorsed";"%JAVA_HOME%\lib\endorsed"

%_RUNJAVA% %JAVA_OPTS% -cp "%SCG_CLASSPATH%" -Djava.endorsed.dirs=%JAVA_ENDORSED% cgl.iotcloud.core.ServerManager %*
endlocal
:end