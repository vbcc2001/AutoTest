#!/usr/bin/env bash
sudo java -jar /Users/tuzi/AndroidStudioProjects/AutoTest/AutoTest_Proxy_Web/target/resin-pro-3.1.12/lib/resin.jar -conf /Users/tuzi/AndroidStudioProjects/AutoTest/AutoTest_Proxy_Web/target/resin-mac.xml -server AutoTest_Proxy_Web  stop
sudo java -jar /Users/tuzi/AndroidStudioProjects/AutoTest/AutoTest_Proxy_Web/target/resin-pro-3.1.12/lib/resin.jar -conf /Users/tuzi/AndroidStudioProjects/AutoTest/AutoTest_Proxy_Web/target/resin-mac.xml -server AutoTest_Proxy_Web  start
