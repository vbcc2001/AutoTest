#!/bin/sh
ulimit -n 357353
PROJECT_PATH=/srv/AutoTest/AutoTest_Proxy_Web
APP_NAME=AutoTest_Proxy_Web
APP_HOME=$PROJECT_PATH/target
JAVA_HOME=$PROJECT_PATH/target/jdk1.8.0_144/jre
JAVA=$JAVA_HOME/bin/java
export JAVA_HOME
RESIN_HOME=$PROJECT_PATH/target/resin-pro-3.1.12
RESIN_CONF=$PROJECT_PATH/target/resin-linux.xml
RESIN=$RESIN_HOME/lib/resin.jar
USER=root
JAVA_CONF="-Dfile.encoding=UTF-8 -Dapp.home=$APP_HOME -Dapp.name=$APP_NAME -jar $RESIN  -resin-home $RESIN_HOME -conf $RESIN_CONF  -server $APP_NAME "
case "$1" in
  start)
  echo -n "Starting resin:"
  su $USER -c "$JAVA $JAVA_CONF start" 
  if [ $? -eq 0 ]; then
    echo " ."
  else
    echo " failed!"
  fi
	;;
  stop)
  echo -n "Stopping resin"
  su $USER -c "$JAVA $JAVA_CONF stop"
  if [ $? -eq 0 ]; then
    echo " ."
  else
    echo " failed!"
  fi
	;;
  restart)
	$0 stop
	$0 start
	;;
  *)
	echo "Usage: $0 {start|stop|restart}"
	exit 1
esac
exit 0
