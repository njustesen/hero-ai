#!/bin/sh
echo   esecuzione    $1
JNEAT_LOCATION=/home/jes
JAVA_LOCATION=/home/jdk1.3.1_01
CLASSPATH=$JNEAT_LOCATION
CLASSPATH=$CLASSPATH:$JAVA_LOCATION
CLASSPATH=$CLASSPATH:$JAVA_LOCATION/jre/lib/rt.jar
CLASSPATH=$CLASSPATH:$JAVA_LOCATION/lib/tools.jar
CLASSPATH=$CLASSPATH:$JNEAT_LOCATION/gui
export CLASSPATH
echo ' classpath = ' $CLASSPATH
echo ' curr dir   = '  $JNEAT_LOCATION
$JAVA_LOCATION/bin/java  $1




