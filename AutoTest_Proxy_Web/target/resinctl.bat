@echo off
echo 当前目录是：%cd%
set PROJECT_PATH=%cd%
set APP_HOME=%PROJECT_PATH%
set APP_PORT=80
set JAVA_HOME=%PROJECT_PATH%\jdk1.8.0_121\jre
set RESIN_HOME=%PROJECT_PATH%\resin-pro-3.1.12
set APP_NAME=app
title %APP_NAME%
%JAVA_HOME%\bin\java -Dfile.encoding=UTF-8 -Dapp.name=%APP_NAME% -Dapp.home=%APP_HOME% -Dapp.port=%APP_PORT% -jar %RESIN_HOME%\lib\resin.jar -resin-home %RESIN_HOME% -conf %APP_HOME%\resin.xml -server %APP_NAME%
