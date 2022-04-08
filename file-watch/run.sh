#!/bin/bash
#打包
./gradlew assemble
#杀掉原进程
port=6060;
kill -9 `lsof -ti:$port`;
CRTDIR=$(pwd)
#启动新服务
echo $CRTDIR
JARDIR=$CRTDIR"/build/libs/file-watch-0.0.1-SNAPSHOT.jar"
java -jar $JARDIR &