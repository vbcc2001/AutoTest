@echo off
echo %cd%
set JAVA_HOME=%cd%\jdk1.8.0_121
set RESIN_HOME=%cd%\resin-pro-3.1.12
set APP_HOME=%cd%\
set APP_NAME=Auto_Reg_Window
title %APP_NAME%
%JAVA_HOME%\bin\java -Dfile.encoding=UTF-8 -Dapp.name=%APP_NAME% -Dapp.home=%APP_HOME% -jar %RESIN_HOME%\lib\resin.jar -resin-home %RESIN_HOME% -conf %APP_HOME%\resin.xml -server %APP_NAME%
