#!/usr/bin/env bash
java -jar /Users/tuzi/AndroidStudioProjects/AutoTest/AutoTest_CMS_Web/target/resin-pro-3.1.12/lib/resin.jar -conf /Users/tuzi/AndroidStudioProjects/AutoTest/AutoTest_CMS_Web/target/resin-mac.xml -server AutoTest_CMS_Web  stop
java -jar /Users/tuzi/AndroidStudioProjects/AutoTest/AutoTest_CMS_Web/target/resin-pro-3.1.12/lib/resin.jar -conf /Users/tuzi/AndroidStudioProjects/AutoTest/AutoTest_CMS_Web/target/resin-mac.xml -server AutoTest_CMS_Web  start
