#!/bin/sh
echo =================================
echo  自动化部署脚本启动
echo =================================

echo 停止原来运行中的工程
# 项目名
APP_NAME=reggie_take_out 
# 通过项目名停止运行中工程 即获取tpid,再通过tpid结束该进程(连续结束两次,双保险)
tpid=`ps -ef|grep $APP_NAME|grep -v grep|grep -v kill|awk '{print $2}'`
if [ ${tpid} ]; then
    echo 'Stop Process...'
    kill -15 $tpid
fi
sleep 2
tpid=`ps -ef|grep $APP_NAME|grep -v grep|grep -v kill|awk '{print $2}'`
if [ ${tpid} ]; then
    echo 'Kill Process!'
    kill -9 $tpid
else
    echo 'Stop Success!'
fi

echo 准备从Git仓库拉取最新代码
# 进入项目目录
cd /usr/local/reggie_take_out

echo 开始从Git仓库拉取最新代码
git pull
echo 代码拉取完成

echo 开始打包
output=`mvn clean package -Dmaven.test.skip=true`
# 进入编译输出目录
cd target

echo 启动项目
# 后台启动项目并输出日志文档 jar包名: artifactId-version(详见pom.xml)
nohup java -jar reggie_take_out-1.0.jar &> reggie_take_out.log &
echo 项目启动完成
