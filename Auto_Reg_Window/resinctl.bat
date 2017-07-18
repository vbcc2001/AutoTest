@echo off
mklink /J "D:\YTZQ_Web_Dev\YTZQ_New_Web\WebRoot\upload" "D:\YTZQ_Web_Dev\YTZQ_New_Cms\WebRoot\upload"
set JAVA_HOME=D:\YTZQ_Web_Dev\jdk1.8.0_40
set RESIN_HOME=D:\YTZQ_Web_Dev\resin-pro-3.1.12
set APP_HOME=D:\YTZQ_Web_Dev\YTZQ_New_Web
set APP_NAME=YTZQ_New_Web
title %APP_NAME%
%JAVA_HOME%\bin\java -Dfile.encoding=GBK -Dapp.name=%APP_NAME% -Dapp.home=%APP_HOME% -jar %RESIN_HOME%\lib\resin.jar -resin-home %RESIN_HOME% -conf %APP_HOME%\resin.xml -server %APP_NAME% 
