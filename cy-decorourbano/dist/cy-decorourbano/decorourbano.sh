#!/bin/sh

CLASSPATH=./classes:./lib/commons-logging-1.2.jar:./lib/commons-dbcp-1.4.jar:./lib/commons-pool-1.6.jar
CLASSPATH=$CLASSPATH:./lib/mysql-connector-java-5.1.34-bin.jar:./lib/spring-aop-4.0.7.RELEASE.jar:./lib/spring-beans-4.0.7.RELEASE.jar:./lib/spring-boot-1.1.8.RELEASE.jar
CLASSPATH=$CLASSPATH:./lib/spring-boot-autoconfigure-1.1.8.RELEASE.jar:./lib/logback-classic-1.1.2.jar:./lib/logback-core-1.1.2.jar:./lib/slf4j-api-1.7.7.jar
CLASSPATH=$CLASSPATH:./lib/spring-context-4.0.7.RELEASE.jar:./lib/spring-core-4.0.7.RELEASE.jar:./lib/spring-expression-4.0.7.RELEASE.jar:./lib/spring-jdbc-4.0.7.RELEASE.jar
CLASSPATH=$CLASSPATH:./lib/spring-tx-4.0.7.RELEASE.jar

APPID=UrbanBot
CORE_URL=http://localhost:8080/cy-bss-core
USER_ID=urbanbot
PWD=urbabot


echo 'CLASSPATH='$CLASSPATH

java -classpath "$CLASSPATH" org.cysoft.decorourbano.main.DecoroUrbanoMain 2>err.log
